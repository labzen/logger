package cn.labzen.logger.kernel.tile.handle;

import cn.labzen.logger.kernel.tile.*;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class MessagePatternTileManager {

  // todo 缓存大小等参数，可做配置
  private static final LoadingCache<String, List<PlaceholderWrapper>> PLACEHOLDERS;

  static {
    PLACEHOLDERS = Caffeine.newBuilder()
                           .maximumSize(4096)
                           .expireAfterAccess(10, TimeUnit.MINUTES)
                           .build(MessagePatternTileManager::parsePlaceholders);

  }

  private MessagePatternTileManager() {
  }

  public static String transform(String pattern, List<Object> args) {
    List<PlaceholderWrapper> found = PLACEHOLDERS.get(pattern);
    return formatMessage(pattern, found, args);
  }

  private static String formatMessage(String pattern, List<PlaceholderWrapper> placeholders, List<Object> args) {
    if (placeholders.isEmpty()) {
      return pattern;
    }

    StringBuilder sb = new StringBuilder();
    int index = 0;

    for (PlaceholderWrapper placeholder : placeholders) {
      for (int i = index; i < placeholder.startIndex(); i++) {
        sb.append(pattern.charAt(i));
      }

      String convertedValue = tileConvert(placeholder.firstTile(), args);
      sb.append(convertedValue);

      index = placeholder.endIndex() + 1;
    }

    while (index < pattern.length()) {
      sb.append(pattern.charAt(index++));
    }

    return sb.toString();
  }

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

  private static PlaceholderWrapper findNextPlaceholder(String pattern, int startIndex, int foundTimes) {
    if (startIndex > 0 && pattern.charAt(startIndex - 1) == '\\') {
      // 转义字符 \{ 不进行处理
      return null;
    }

    int endIndex = pattern.indexOf('}', startIndex + 1);
    if (endIndex == -1) {
      return null;
    }

    String internalText = pattern.substring(startIndex + 1, endIndex);
    HeadTile<?> tiles = parsePlaceholderTiles(internalText, foundTimes);
    return new PlaceholderWrapper(foundTimes, startIndex, endIndex, internalText, tiles);
  }

  private static HeadTile<?> parsePlaceholderTiles(String text, int foundTimes) {
    boolean mightExistTiles = text.contains("@");
    if (mightExistTiles) {
      return parseFunctionTiles(text, foundTimes);
    } else {
      return parseSimpleTile(text, foundTimes);
    }
  }

  private static HeadTile<?> parseFunctionTiles(String text, int foundTimes) {
    int firstTileStartIndex = text.indexOf("@");
    HeadTile<?> tileHead;
    if (firstTileStartIndex > 0) {
      String beforeText = text.substring(0, firstTileStartIndex);
      tileHead = parseSimpleTile(beforeText, foundTimes);
    } else {
      tileHead = new DefaultTile(foundTimes);
    }

    parseFunctionTile(text, firstTileStartIndex, tileHead);

    return tileHead;
  }

  private static void parseFunctionTile(String text, int startIndex, Tile<?> prevTile) {
    int nextTileStartIndex = text.indexOf("@", startIndex + 1);
    int endIndex;
    if (nextTileStartIndex < 0) {
      // 后面没有 Tile 了
      endIndex = text.length();
    } else {
      endIndex = nextTileStartIndex;
    }

    String tileText = text.substring(startIndex, endIndex);
    AbstractTile<?> tile = AbstractTile.match(tileText);
    if (tile == null) {
      tile = new WrongTile("WRONG_TILE[" + tileText + "]");
    }
    prevTile.setNext(tile);

    if (nextTileStartIndex > 0) {
      parseFunctionTile(text, nextTileStartIndex, tile);
    }
  }

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
