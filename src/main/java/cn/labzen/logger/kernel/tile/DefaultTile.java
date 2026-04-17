package cn.labzen.logger.kernel.tile;

import java.util.*;

/**
 * 默认占位符Tile，处理无前缀的{}占位符。
 *
 * <p>支持的参数类型：
 * <ul>
 *   <li>Object[] - 数组，按下标提取元素</li>
 *   <li>List - 列表，按下标提取元素</li>
 *   <li>Map - Map转列表后按下标提取元素</li>
 *   <li>其他类型 - 直接作为值返回（仅当position=0时）</li>
 * </ul>
 *
 * <p>占位符格式：{} 或带空格的{}
 *
 * <p>使用示例：
 * <pre>
 * logger.info("用户:{} 分数:{}", "Alice", 95);
 * // 输出：用户:Alice 分数:95
 * </pre>
 *
 * @see PositionTile
 * @see NamedTile
 * @see HeadTile
 */
public class DefaultTile extends AbstractTile<Object> implements HeadTile<Object> {

  /** 在参数列表中的位置索引 */
  private final int position;

  /**
   * 构造方法
   *
   * @param position 参数位置索引，从0开始
   */
  public DefaultTile(int position) {
    this.position = position;
  }

  /**
   * 从参数中提取指定位置的元素
   *
   * <p>处理逻辑：
   * <ol>
   *   <li>如果是数组：转为列表后按position提取</li>
   *   <li>如果是列表：直接按position提取</li>
   *   <li>如果是Map：转为values列表后按position提取</li>
   *   <li>其他类型：仅当position=0时返回原值，否则返回空字符串</li>
   * </ol>
   *
   * @param value 输入参数对象
   * @return 提取的元素，position越界返回null，其他情况返回空字符串
   */
  @Override
  public Object convert(Object value) {
    switch (value) {
      case Object[] array -> {
        // 数组情况：如果只有一个元素且是List，则取该List
        if (array.length == 1 && array[0] instanceof List<?> list) {
          return safeTake(list, position);
        } else {
          return safeTake(Arrays.asList(array), position);
        }
      }
      case List<?> list -> {
        return safeTake(list, position);
      }
      case Map<?, ?> map -> {
        return safeTake(new ArrayList<>(map.values()), position);
      }
      case null, default -> {
        // 其他类型：只有position=0时才返回原值
        return position == 0 ? value : "";
      }
    }
  }

  /**
   * 安全提取列表元素
   *
   * @param list     源列表
   * @param position 位置索引
   * @return 元素值或null（越界时）
   */
  private Object safeTake(List<?> list, int position) {
    return position < list.size() ? list.get(position) : null;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultTile that = (DefaultTile) o;
    return position == that.position;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(position);
  }
}