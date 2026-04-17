package cn.labzen.logger.kernel.tile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tile抽象基类，提供链式结构的通用实现。
 *
 * <p>职责：
 * <ul>
 *   <li>维护next引用，形成Tile链</li>
 *   <li>提供静态工厂方法，根据文本匹配创建对应的Tile实例</li>
 * </ul>
 *
 * <p>支持的Tile类型及其格式：
 * <table>
 *   <tr><th>类型</th><th>格式</th><th>示例</th></tr>
 *   <tr><td>数字格式化</td><td>{@code @number_pattern}</td><td>{@code @number_0.00}</td></tr>
 *   <tr><td>日期格式化</td><td>{@code @date_pattern}</td><td>{@code @date_yyyy-MM-dd}</td></tr>
 *   <tr><td>字符包裹</td><td>{@code @wrap_xx}</td><td>{@code @wrap_[]}</td></tr>
 *   <tr><td>布尔条件</td><td>{@code @whether_true,false}</td><td>{@code @whether_是,否}</td></tr>
 *   <tr><td>宽度控制</td><td>{@code @width_min[,max]}</td><td>{@code @width_10,20}</td></tr>
 * </table>
 *
 * @param <R> 转换结果的类型
 * @see Tile
 * @see HeadTile
 * @see DefaultTile
 * @see FormatterOfNumberTile
 * @see FormatterOfDateTile
 */
public abstract class AbstractTile<R> implements Tile<R> {

  /** 数字格式化正则：匹配 @number_pattern */
  private static final Pattern TILE_FORMAT_NUMBER_REGEX = Pattern.compile("^@number_(.*)$");

  /** 日期格式化正则：匹配 @date_pattern */
  private static final Pattern TILE_FORMAT_DATE_REGEX = Pattern.compile("^@date_(.*)$");

  /** 字符包裹正则：匹配 @wrap_XX（两个字符） */
  private static final Pattern TILE_WRAP_REGEX = Pattern.compile("^@wrap_(.{2})$");

  /** 布尔条件正则：匹配 @whether_true,false */
  private static final Pattern TILE_WHETHER_REGEX = Pattern.compile("^@whether_(.*),(.*)$");

  /** 宽度控制正则：匹配 @width_min[,max] */
  private static final Pattern TILE_WIDTH_REGEX = Pattern.compile("^@width_(\\d+)(,(\\d+))?$");

  /** 指向链中下一个Tile的引用 */
  private Tile<?> next;

  /**
   * 设置下一个Tile，构建处理链
   *
   * @param next 下一个Tile节点
   */
  @Override
  public void setNext(Tile<?> next) {
    this.next = next;
  }

  /**
   * 获取链中的下一个Tile
   *
   * @return 下一个Tile，null表示是链的最后节点
   */
  @Override
  public Tile<?> getNext() {
    return next;
  }

  /**
   * 判断是否存在下一个Tile
   *
   * @return true表示链未结束
   */
  @Override
  public boolean hasNext() {
    return next != null;
  }

  /**
   * 根据文本匹配创建对应的Tile实例
   *
   * <p>匹配规则按优先级顺序尝试：
   * <ol>
   *   <li>{@code @number_pattern} → {@link FormatterOfNumberTile}</li>
   *   <li>{@code @date_pattern} → {@link FormatterOfDateTile}</li>
   *   <li>{@code @wrap_XX} → {@link FunctionalWrapTile}</li>
   *   <li>{@code @whether_true,false} → {@link FunctionalWhetherTile}</li>
   *   <li>{@code @width_min[,max]} → {@link FunctionalWidthControlTile}</li>
   * </ol>
   *
   * <p>如果都不匹配，返回null（由调用方处理）
   *
   * @param text Tile配置文本
   * @return 对应的Tile实例，null表示不匹配任何已知类型
   */
  public static AbstractTile<?> match(String text) {
    if (TILE_FORMAT_NUMBER_REGEX.matcher(text).matches()) {
      Matcher matcher = TILE_FORMAT_NUMBER_REGEX.matcher(text);
      if (matcher.find()) {
        return new FormatterOfNumberTile(matcher.group(1));
      }
    } else if (TILE_FORMAT_DATE_REGEX.matcher(text).matches()) {
      Matcher matcher = TILE_FORMAT_DATE_REGEX.matcher(text);
      if (matcher.find()) {
        return new FormatterOfDateTile(matcher.group(1));
      }
    } else if (TILE_WRAP_REGEX.matcher(text).matches()) {
      Matcher matcher = TILE_WRAP_REGEX.matcher(text);
      if (matcher.find()) {
        return new FunctionalWrapTile(matcher.group(1));
      }
    } else if (TILE_WHETHER_REGEX.matcher(text).matches()) {
      Matcher matcher = TILE_WHETHER_REGEX.matcher(text);
      if (matcher.find()) {
        return new FunctionalWhetherTile(matcher.group(1), matcher.group(2));
      }
    } else if (TILE_WIDTH_REGEX.matcher(text).matches()) {
      Matcher matcher = TILE_WIDTH_REGEX.matcher(text);
      if (matcher.find()) {
        int min = Integer.parseInt(matcher.group(1));
        String maxGroup = matcher.group(3);
        int max = (maxGroup == null || maxGroup.isBlank()) ? 0 : Integer.parseInt(maxGroup);
        return new FunctionalWidthControlTile(min, max);
      }
    }
    return null;
  }
}