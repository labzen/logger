package cn.labzen.logger.kernel.tile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 命名占位符Tile，用于从Map中按名称提取参数。
 *
 * <p>占位符格式：{param_name}
 *
 * <p>参数要求：
 * <ul>
 *   <li>输入必须是List类型</li>
 *   <li>List必须只有一个元素且为Map类型</li>
 *   <li>从该Map中按key获取值</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * Map&lt;String, Object&gt; params = Map.of("name", "Alice", "age", 25);
 * logger.info("用户:{name}", params);
 * // 输出：用户:Alice
 * </pre>
 *
 * @see DefaultTile
 * @see PositionTile
 * @see HeadTile
 */
public class NamedTile extends AbstractTile<Object> implements HeadTile<Object> {

  /** 参数名称key */
  private final String key;

  /**
   * 构造方法
   *
   * @param key 参数名称
   */
  public NamedTile(String key) {
    this.key = key;
  }

  /**
   * 从Map中按名称提取值
   *
   * <p>输入必须是List[List[1个Map]]结构
   *
   * @param value 输入参数（必须是List[List[Map]]）
   * @return Map中对应key的值，未找到返回null
   */
  @Override
  public Object convert(Object value) {
    if (value instanceof List<?> list && list.size() == 1 && list.getFirst() instanceof Map<?, ?> first) {
      return first.get(key);
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NamedTile namedTile = (NamedTile) o;
    return Objects.equals(key, namedTile.key);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key);
  }
}
