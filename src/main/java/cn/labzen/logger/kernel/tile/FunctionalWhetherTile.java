package cn.labzen.logger.kernel.tile;

import java.util.Objects;

/**
 * 布尔条件Tile，根据布尔值输出不同的文本。
 *
 * <p>占位符格式：{@code {param@whether_trueText,falseText}}
 *
 * <p>规则：
 * <ul>
 *   <li>true时输出第一个参数</li>
 *   <li>false时输出第二个参数</li>
 *   <li>非Boolean类型输出"non"</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * boolean success = false;
 * logger.info("结果:&lcub;@whether_成功,失败}", success);  // 输出：结果:失败
 * </pre>
 *
 * @see AbstractTile
 */
public class FunctionalWhetherTile extends AbstractTile<String> {

  /** true时输出的文本 */
  private final String trueText;

  /** false时输出的文本 */
  private final String falseText;

  /**
   * 构造方法
   *
   * @param trueText  true时输出
   * @param falseText false时输出
   */
  public FunctionalWhetherTile(String trueText, String falseText) {
    this.trueText = trueText;
    this.falseText = falseText;
  }

  /**
   * 根据布尔值选择输出文本
   *
   * @param value 输入值
   * @return trueText/falseText/non
   */
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
