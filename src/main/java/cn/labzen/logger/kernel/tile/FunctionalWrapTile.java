package cn.labzen.logger.kernel.tile;

import java.util.Objects;

/**
 * 字符包裹Tile，用指定字符包裹值输出。
 *
 * <p>占位符格式：{@code {param@wrap_XX}}
 *
 * <p>规则：
 * <ul>
 *   <li>必须提供恰好2个字符</li>
 *   <li>第一个字符作为左包裹符</li>
 *   <li>第二个字符作为右包裹符</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * String name = "Alice";
 * logger.info("用户:&lcub;@wrap_()} ", name);  // 输出：用户:(Alice)
 * logger.info("用户:&lcub;@wrap_[]} ", name); // 输出：用户:[Alice]
 * </pre>
 *
 * @see AbstractTile
 */
public class FunctionalWrapTile extends AbstractTile<String> {

  /** 左右包裹字符（各1个字符） */
  private final String edge;

  /**
   * 构造方法
   *
   * @param edge 两个字符的包裹符，如"()"、"[]"、"{}"
   * @throws IllegalArgumentException 如果不是2个字符
   */
  public FunctionalWrapTile(String edge) {
    if (edge == null || edge.length() != 2) {
      throw new IllegalArgumentException("requires 2 characters");
    }
    this.edge = edge;
  }

  /**
   * 包裹值
   *
   * @param value 输入值
   * @return 包裹后的字符串，如"value" → "(value)"
   */
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
