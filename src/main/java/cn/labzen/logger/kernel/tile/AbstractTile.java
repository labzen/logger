package cn.labzen.logger.kernel.tile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractTile<R> implements Tile<R> {

  private static final Pattern TILE_FORMAT_NUMBER_REGEX = Pattern.compile("^@number_(.*)$");
  private static final Pattern TILE_FORMAT_DATE_REGEX = Pattern.compile("^@date_(.*)$");
  private static final Pattern TILE_WRAP_REGEX = Pattern.compile("^@wrap_(.{2})$");
  private static final Pattern TILE_WHETHER_REGEX = Pattern.compile("^@whether_(.*),(.*)$");
  private static final Pattern TILE_WIDTH_REGEX = Pattern.compile("^@width_(\\d+)(,(\\d+))?$");

  private Tile<?> next;

  @Override
  public void setNext(Tile<?> next) {
    this.next = next;
  }

  @Override
  public Tile<?> getNext() {
    return next;
  }

  @Override
  public boolean hasNext() {
    return next != null;
  }

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
