package cn.labzen.logger.kernel.tile;

/**
 * 空操作Tile，不执行任何转换，直接返回空字符串。
 *
 * <p>用途：
 * <ul>
 *   <li>作为链的末尾节点，避免null检查</li>
 *   <li>占位符解析失败时的默认返回</li>
 * </ul>
 *
 * @see Tile
 * @see HeadTile
 */
public class NopTile extends AbstractTile<Object> implements HeadTile<Object> {

  /** 单例实例 */
  public static final NopTile INSTANCE = new NopTile();

  /**
   * 返回空字符串
   *
   * @param value 输入值（被忽略）
   * @return 空字符串
   */
  @Override
  public Object convert(Object value) {
    return "";
  }
}