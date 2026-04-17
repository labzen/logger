package cn.labzen.logger.kernel.tile;

/**
 * Tile（瓦片）接口，定义消息占位符转换的基本操作。
 *
 * <p>Tile是消息处理流水线中的基本单元，每个Tile负责：
 * <ul>
 *   <li>接收输入值</li>
 *   <li>执行某种转换或格式化</li>
 *   <li>将结果传递给下一个Tile（如果有）</li>
 * </ul>
 *
 * <p>Tile链式调用示例：
 * <pre>
 * DefaultTile -> FunctionalWrapTile -> FunctionalWidthControlTile
 *     │              │                        │
 *     ▼              ▼                        ▼
 *  提取参数         包裹字符                 控制输出宽度
 * </pre>
 *
 * <p>实现类：
 * <ul>
 *   <li>{@link HeadTile} - 链首Tile，标记链的开始</li>
 *   <li>{@link AbstractTile} - 抽象基类，提供链式结构实现</li>
 *   <li>{@link DefaultTile} - 默认占位符{}</li>
 *   <li>{@link NamedTile} - 命名占位符{name}</li>
 *   <li>{@link PositionTile} - 位置占位符{0}</li>
 *   <li>{@link FormatterOfDateTile} - 日期格式化</li>
 *   <li>{@link FormatterOfNumberTile} - 数字格式化</li>
 *   <li>{@link FunctionalWrapTile} - 字符包裹</li>
 *   <li>{@link FunctionalWhetherTile} - 布尔条件</li>
 *   <li>{@link FunctionalWidthControlTile} - 宽度控制</li>
 * </ul>
 *
 * @param <R> 转换结果的类型
 * @see HeadTile
 * @see AbstractTile
 */
public interface Tile<R> {

  /**
   * 设置下一个Tile，形成处理链
   *
   * <p>处理流程：当前Tile处理完成后，将结果传递给下一个Tile继续处理
   *
   * @param next 下一个Tile，不能为null表示链结束
   */
  void setNext(Tile<?> next);

  /**
   * 获取链中的下一个Tile
   *
   * @return 下一个Tile实例，null表示当前是链的最后节点
   */
  Tile<?> getNext();

  /**
   * 判断是否存在下一个Tile
   *
   * @return true表示链未结束，还有后续处理
   */
  boolean hasNext();

  /**
   * 执行值转换
   *
   * <p>转换过程可能涉及：
   * <ul>
   *   <li>类型转换（如Number转String）</li>
   *   <li>格式化（如日期、数字格式）</li>
   *   <li>内容处理（如包裹字符、截断）</li>
   * </ul>
   *
   * <p>转换完成后，如果存在下一个Tile，自动将结果传递给它继续处理
   *
   * @param value 输入值，可能是任何类型
   * @return 转换后的结果，类型由具体实现决定
   */
  R convert(Object value);
}
