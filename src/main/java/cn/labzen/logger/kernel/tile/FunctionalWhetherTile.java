package cn.labzen.logger.kernel.tile;

import java.util.Objects;

/**
 * 函数式占位，仅适用于boolean类型的参数，格式：&#123;@whether_yes,no}
 */
public class FunctionalWhetherTile extends AbstractTile<String> {

  private final String trueText;
  private final String falseText;

  public FunctionalWhetherTile(String trueText, String falseText) {
    this.trueText = trueText;
    this.falseText = falseText;
  }

  @Override
  public String convert(Object value) {
    if (value instanceof Boolean) {
      return (Boolean) value ? trueText : falseText;
    }
    return "non";
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FunctionalWhetherTile that = (FunctionalWhetherTile) o;
    return Objects.equals(trueText, that.trueText) && Objects.equals(falseText, that.falseText);
  }

  @Override
  public int hashCode() {
    return Objects.hash(trueText, falseText);
  }
}
