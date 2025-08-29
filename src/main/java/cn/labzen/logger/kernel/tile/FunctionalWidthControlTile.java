package cn.labzen.logger.kernel.tile;

import java.util.Objects;

public class FunctionalWidthControlTile extends AbstractTile<String> {

  private final Integer min;
  private final Integer max;

  public FunctionalWidthControlTile(Integer min, Integer max) {
    if (min == null && max == null) {
      throw new IllegalArgumentException("at least 1 parameter is defined");
    }
    this.min = min;
    this.max = max;
  }

  @Override
  public String convert(Object value) {
    String string = value.toString();
    int length = string.length();
    if (min == null) {
      if (length > max) {
        return string.substring(0, max);
      } else {
        return string;
      }
    } else if (max == null) {
      if (length < min) {
        return padEnd(string, min);
      } else {
        return string;
      }
    } else {
      if (length > max) {
        return string.substring(0, max);
      } else if (length < min) {
        return padEnd(string, min);
      } else {
        return string;
      }
    }
  }

  private String padEnd(String str, int length) {
    if (str.length() >= length) {
      return str;
    }
    return str + String.valueOf(' ').repeat(length - str.length());
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FunctionalWidthControlTile that = (FunctionalWidthControlTile) o;
    return Objects.equals(min, that.min) && Objects.equals(max, that.max);
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max);
  }
}
