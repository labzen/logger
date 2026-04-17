package cn.labzen.logger.kernel.tile;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数字格式化Tile，将数值按指定模式格式化输出。
 *
 * <p>占位符格式：{@code {param@number_pattern}}
 *
 * <p>支持的格式模式参考{@link java.text.DecimalFormat}：
 * <ul>
 *   <li>"0" - 数字位，不存在时补零</li>
 *   <li>"#" - 数字位，不存在时不补零</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * double price = 16.7;
 * logger.info("价格:&lcub;@number_0.00}", price);  // 输出：价格:16.70
 * logger.info("价格:&lcub;@number_000.##}", price); // 输出：价格:016.7
 * </pre>
 *
 * @see java.text.DecimalFormat
 * @see AbstractTile
 */
public class FormatterOfNumberTile extends AbstractTile<String> {

  private static final Map<String, DecimalFormat> FORMATTER_CACHE = new ConcurrentHashMap<>();

  /** 数字格式化器 */
  private final NumberFormat formatter;

  /**
   * 构造方法
   *
   * @param pattern 格式化模式，如"0.00"、".##"等
   */
  public FormatterOfNumberTile(String pattern) {
    formatter = FORMATTER_CACHE.computeIfAbsent(pattern, DecimalFormat::new);
  }

  /**
   * 格式化数字值
   *
   * <p>处理规则：
   * <ul>
   *   <li>如果是Number类型，执行格式化</li>
   *   <li>其他类型调用toString()</li>
   * </ul>
   *
   * @param value 输入值
   * @return 格式化后的字符串
   */
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
