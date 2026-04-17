package cn.labzen.logger.kernel.tile;

/**
 * 位置占位符Tile，用于从参数列表中按位置提取元素。
 *
 * <p>占位符格式：{0}、{1}、{2}...
 *
 * <p>与{@link DefaultTile}的区别：
 * <ul>
 *   <li>DefaultTile处理{}，自动按顺序分配位置</li>
 *   <li>PositionTile处理{position}，显式指定位置</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * logger.info("第三个参数是:{2}", "a", "b", "c");
 * // 输出：第三个参数是:c
 * </pre>
 *
 * @see DefaultTile
 * @see NamedTile
 * @see HeadTile
 */
public class PositionTile extends DefaultTile {

  /**
   * 构造方法
   *
   * @param position 参数位置索引，从0开始
   */
  public PositionTile(int position) {
    super(position);
  }
}