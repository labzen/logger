package cn.labzen.logger.kernel.tile.handle;

import cn.labzen.logger.kernel.tile.*;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 消息模板占位符管理器，负责解析和转换带占位符的消息模板。
 *
 * <p>核心功能：
 * <ul>
 *   <li>解析消息模板中的占位符（如{}、{0}、{name}）</li>
 *   <li>解析占位符的修饰符（如@number_0.00、@wrap_[]）</li>
 *   <li>将解析结果缓存以提升性能</li>
 *   <li>执行值转换，生成最终消息</li>
 * </ul>
 *
 * <p>Tile链处理流程：
 * <pre>
 * 输入参数 → HeadTile → [FunctionalTile...] → 输出字符串
 *                ↑              ↑
 *            参数提取        格式化/转换
 * </pre>
 *
 * @see Tile
 * @see HeadTile
 * @see PlaceholderWrapper
 */
public final class MessagePatternTileManager {

  /**
   * 消息模板缓存
   *
   * <p>Key: 消息模板字符串
   * <p>Value: 占位符列表，每个占位符包含位置信息和对应的Tile链
   */
  // todo 缓存大小等参数，可做配置
  private static final LoadingCache<String, List<PlaceholderWrapper>> PLACEHOLDERS;

  static {
    PLACEHOLDERS = Caffeine.newBuilder()
                           .maximumSize(4096)
                           .expireAfterAccess(10, TimeUnit.MINUTES)
                           .build(MessagePatternTileManager::parsePlaceholders);
  }

  /**
   * 私有构造方法，防止实例化
   */
  private MessagePatternTileManager() {
  }

  /**
   * 转换消息模板
   *
   * <p>处理流程：
   * <ol>
   *   <li>从缓存获取占位符列表（缓存未命中时触发解析）</li>
   *   <li>遍历占位符，替换为转换后的值</li>
   *   <li>拼接非占位符文本</li>
   * </ol>
   *
   * @param pattern 消息模板，如"用户:{} 分数:{}"
   * @param args    参数列表
   * @return 转换后的最终字符串
   */
  public static String transform(String pattern, List<Object> args) {
    List<PlaceholderWrapper> found = PLACEHOLDERS.get(pattern);
    assert found != null;
    return formatMessage(pattern, found, args);
  }

  /**
   * 格式化消息，替换占位符
   *
   * <p>遍历占位符列表，逐个替换为转换后的值
   *
   * @param pattern      原始模板
   * @param placeholders 占位符信息列表
   * @param args         参数列表
   * @return 格式化后的字符串
   */
  private static String formatMessage(String pattern, List<PlaceholderWrapper> placeholders, List<Object> args) {
    if (placeholders.isEmpty()) {
      return pattern;
    }

    StringBuilder sb = new StringBuilder();
    int index = 0;

    // 遍历每个占位符
    for (PlaceholderWrapper placeholder : placeholders) {
      // 1. 追加占位符之前的文本
      for (int i = index; i < placeholder.startIndex(); i++) {
        sb.append(pattern.charAt(i));
      }

      // 2. 转换占位符值并追加
      String convertedValue = tileConvert(placeholder.firstTile(), args);
      sb.append(convertedValue);

      // 3. 更新索引位置
      index = placeholder.endIndex() + 1;
    }

    // 4. 追加最后一个占位符之后的文本
    while (index < pattern.length()) {
      sb.append(pattern.charAt(index++));
    }

    return sb.toString();
  }

  /**
   * 通过Tile链转换参数值
   *
   * <p>从HeadTile开始，依次调用每个Tile的convert方法进行转换
   *
   * @param tile HeadTile（链首）
   * @param args 参数列表
   * @return 转换后的字符串，null时返回空字符串
   */
  private static String tileConvert(HeadTile<?> tile, List<Object> args) {
    if (tile == null) {
      return "";
    }

    Tile<?> pointer = tile;
    Object converted = args;
    while (pointer != null) {
      converted = pointer.convert(converted);
      pointer = pointer.getNext();
    }

    if (converted == null) {
      return "";
    }
    return converted.toString();
  }

  // ===============================================================================================

  /**
   * 解析消息模板中的所有占位符
   *
   * <p>扫描整个模板，查找所有{}形式的占位符
   *
   * @param pattern 消息模板
   * @return 占位符信息列表
   */
  private static List<PlaceholderWrapper> parsePlaceholders(String pattern) {
    List<PlaceholderWrapper> placeholders = new ArrayList<PlaceholderWrapper>();
    int foundTimes = 0;
    int i = 0;

    while (i < pattern.length()) {
      if (pattern.charAt(i) == '{') {
        PlaceholderWrapper placeholder = findNextPlaceholder(pattern, i, foundTimes);
        if (placeholder != null) {
          placeholders.add(placeholder);
          foundTimes++;
          i = placeholder.endIndex();
          continue;
        }
      }
      i++;
    }

    return placeholders;
  }

  /**
   * 查找并解析下一个占位符
   *
   * <p>处理转义字符：\{ 不作为占位符处理
   *
   * @param pattern    消息模板
   * @param startIndex 左括号位置
   * @param foundTimes 当前已找到的占位符数量（用于默认值分配）
   * @return 占位符信息，未找到或转义时返回null
   */
  private static PlaceholderWrapper findNextPlaceholder(String pattern, int startIndex, int foundTimes) {
    // 检查转义字符
    if (startIndex > 0 && pattern.charAt(startIndex - 1) == '\\') {
      // 转义字符 \{ 不进行处理
      return null;
    }

    // 查找匹配的右括号
    int endIndex = pattern.indexOf('}', startIndex + 1);
    if (endIndex == -1) {
      return null;
    }

    // 提取占位符内部内容并解析
    String internalText = pattern.substring(startIndex + 1, endIndex);
    HeadTile<?> tiles = parsePlaceholderTiles(internalText, foundTimes);
    return new PlaceholderWrapper(foundTimes, startIndex, endIndex, internalText, tiles);
  }

  /**
   * 解析占位符内部内容，构建Tile链
   *
   * <p>判断规则：
   * <ul>
   *   <li>包含@：使用函数式Tile解析</li>
   *   <li>不包含@：使用简单Tile解析（位置或命名）</li>
   * </ul>
   *
   * @param text       占位符内部文本
   * @param foundTimes 占位符序号（用于默认值分配）
   * @return 链首Tile
   */
  private static HeadTile<?> parsePlaceholderTiles(String text, int foundTimes) {
    boolean mightExistTiles = text.contains("@");
    if (mightExistTiles) {
      return parseFunctionTiles(text, foundTimes);
    } else {
      return parseSimpleTile(text, foundTimes);
    }
  }

  /**
   * 解析函数式Tile（带@修饰符）
   *
   * <p>格式：{param@function1@function2@...}
   *
   * @param text       占位符文本
   * @param foundTimes 占位符序号
   * @return 链首Tile
   */
  private static HeadTile<?> parseFunctionTiles(String text, int foundTimes) {
    int firstTileStartIndex = text.indexOf("@");
    HeadTile<?> tileHead;

    // @前有内容，创建HeadTile
    if (firstTileStartIndex > 0) {
      String beforeText = text.substring(0, firstTileStartIndex);
      tileHead = parseSimpleTile(beforeText, foundTimes);
    } else {
      // @开头，使用默认位置
      tileHead = new DefaultTile(foundTimes);
    }

    parseFunctionTile(text, firstTileStartIndex, tileHead);

    return tileHead;
  }

  /**
   * 递归解析函数式Tile
   *
   * <p>从startIndex开始，解析每个@function并构建Tile链
   *
   * @param text       占位符文本
   * @param startIndex 当前@的位置
   * @param prevTile   前一个Tile（用于链接）
   */
  private static void parseFunctionTile(String text, int startIndex, Tile<?> prevTile) {
    // 查找下一个@的位置
    int nextTileStartIndex = text.indexOf("@", startIndex + 1);
    int endIndex;
    if (nextTileStartIndex < 0) {
      // 后面没有Tile了
      endIndex = text.length();
    } else {
      endIndex = nextTileStartIndex;
    }

    // 提取Tile配置文本
    String tileText = text.substring(startIndex, endIndex);
    AbstractTile<?> tile = AbstractTile.match(tileText);
    if (tile == null) {
      // 无法识别，创建错误Tile
      tile = new WrongTile("WRONG_TILE[" + tileText + "]");
    }
    prevTile.setNext(tile);

    // 递归处理下一个Tile
    if (nextTileStartIndex > 0) {
      parseFunctionTile(text, nextTileStartIndex, tile);
    }
  }

  /**
   * 解析简单Tile（无@修饰符）
   *
   * <p>判断规则：
   * <ul>
   *   <li>空白文本：使用默认位置</li>
   *   <li>纯数字：使用PositionTile</li>
   *   <li>其他文本：使用NamedTile</li>
   * </ul>
   *
   * @param text       简单Tile文本
   * @param foundTimes 占位符序号
   * @return 对应的Tile
   */
  private static HeadTile<?> parseSimpleTile(String text, int foundTimes) {
    if (text.isBlank()) {
      return new DefaultTile(foundTimes);
    }

    try {
      int position = Integer.parseInt(text.trim());
      return new PositionTile(position);
    } catch (NumberFormatException e) {
      return new NamedTile(text.trim());
    }
  }
}
