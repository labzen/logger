package cn.labzen.logger.kernel.tile;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

/**
 * 格式化占位，数字格式化占位，格式：&#123;@number_0.00}
 */
public class FormatterOfNumberTile extends AbstractTile<String> {

  private final NumberFormat formatter;

  public FormatterOfNumberTile(String pattern) {
    formatter = new DecimalFormat(pattern);
  }

  @Override
  public String convert(Object value) {
    if (value instanceof Number) {
      return formatter.format(value);
    }
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FormatterOfNumberTile that = (FormatterOfNumberTile) o;
    return Objects.equals(formatter, that.formatter);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(formatter);
  }
}
