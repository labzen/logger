package cn.labzen.logger.kernel.tile;

/**
 * 链首Tile标记接口，用于标识Tile链的入口节点。
 *
 * <p>设计目的：
 * <ul>
 *   <li>区分链首和普通Tile，普通Tile只能作为后续节点</li>
 *   <li>HeadTile是参数提取的起点</li>
 *   <li>HeadTile的实现类负责从参数列表/Map中提取原始值</li>
 * </ul>
 *
 * <p>实现类必须是{@link Tile}链的第一个节点：
 * <ul>
 *   <li>{@link DefaultTile} - {}默认占位符</li>
 *   <li>{@link PositionTile} - {0}位置占位符</li>
 *   <li>{@link NamedTile} - {name}命名占位符</li>
 *   <li>{@link NopTile} - 空操作Tile</li>
 * </ul>
 *
 * @param <R> 转换结果的类型参数
 * @see Tile
 * @see DefaultTile
 * @see PositionTile
 * @see NamedTile
 */
public interface HeadTile<R> extends Tile<R> {

}