package cn.labzen.logger.kernel.tile;

import java.util.Objects;

/**
 * 宽度控制Tile，控制输出的最小和最大宽度。
 *
 * <p>占位符格式：{@code {param@width_min[,max]}}
 *
 * <p>参数说明：
 * <ul>
 *   <li>min - 最小宽度，不足时右侧补空格</li>
 *   <li>max - 最大宽度，超出时截断</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * String text = "Hello";
 * logger.info("&lcub;@width_10}", text);   // 输出："Hello     "（右侧补5空格）
 * logger.info("&lcub;@width_3,8}", text);   // 输出："Hello"（在3-8范围内，保持原样）
 * logger.info("&lcub;@width_,4}", text);     // 输出："Hell"（截断到4字符）
 * </pre>
 *
 * @see AbstractTile
 */
public class FunctionalWidthControlTile extends AbstractTile<String> {

  /** 最小宽度，null表示不限制 */
  private final Integer min;

  /** 最大宽度，null表示不限制 */
  private final Integer max;

  /**
   * 构造方法
   *
   * @param min 最小宽度，null表示不限制
   * @param max 最大宽度，null表示不限制
   * @throws IllegalArgumentException 如果两个都为null
   */
  public FunctionalWidthControlTile(Integer min, Integer max) {
    if (min == null && max == null) {
      throw new IllegalArgumentException("at least 1 parameter is defined");
    }
    this.min = min;
    this.max = max;
  }

  /**
   * 控制输出宽度
   *
   * <p>处理逻辑：
   * <ul>
   *   <li>长度 > max：截断到max</li>
   *   <li>长度 < min：右侧补空格到min</li>
   *   <li>min <= 长度 <= max：保持原样</li>
   * </ul>
   *
   * @param value 输入值
   * @return 处理后的字符串
   */
  @Override
  public String convert(Object value) {
    String string = value.toString();
    int length = string.length();

    // 只有最大宽度限制
    if (min == null) {
      if (length > max) {
        return string.substring(0, max);
      } else {
        return string;
      }
    }
    // 只有最小宽度限制
    else if (max == null) {
      if (length < min) {
        return padEnd(string, min);
      } else {
        return string;
      }
    }
    // 同时有最小和最大宽度限制
    else {
      if (length > max) {
        return string.substring(0, max);
      } else if (length < min) {
        return padEnd(string, min);
      } else {
        return string;
      }
    }
  }

  /**
   * 右侧补空格
   *
   * @param str    源字符串
   * @param length 目标长度
   * @return 补齐后的字符串
   */
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
