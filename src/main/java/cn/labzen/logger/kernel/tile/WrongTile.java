package cn.labzen.logger.kernel.tile;

import java.util.Objects;

/**
 * 错误的占位符，用于标记 log pattern 中 tile 使用错误
 */
public class WrongTile extends AbstractTile<String> {

  private final String message;

  public WrongTile(String message) {
    this.message = message;
  }

  @Override
  public String convert(Object value) {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WrongTile wrongTile = (WrongTile) o;
    return Objects.equals(message, wrongTile.message);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(message);
  }
}
