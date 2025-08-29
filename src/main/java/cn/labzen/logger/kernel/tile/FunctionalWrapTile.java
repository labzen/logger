package cn.labzen.logger.kernel.tile;

import java.util.Objects;

/**
 * 函数式占位，将参数使用给定的字符包裹输出，格式：&#123;@wrap_[]}
 */
public class FunctionalWrapTile extends AbstractTile<String> {

  private final String edge;

  public FunctionalWrapTile(String edge) {
    if (edge == null || edge.length() != 2) {
      throw new IllegalArgumentException("requires 2 characters");
    }
    this.edge = edge;
  }

  @Override
  public String convert(Object value) {
    return edge.charAt(0) + value.toString() + edge.charAt(1);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FunctionalWrapTile that = (FunctionalWrapTile) o;
    return Objects.equals(edge, that.edge);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(edge);
  }
}
