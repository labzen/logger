package cn.labzen.logger.kernel.tile;

import java.util.Objects;

/**
 * 错误占位符Tile，标记无效的占位符格式。
 *
 * <p>用途：
 * <ul>
 *   <li>当占位符格式无法匹配任何已知Tile时使用</li>
 *   <li>在输出中显示错误信息，便于调试</li>
 * </ul>
 *
 * <p>示例输出：
 * <pre>
 * WRONG_TILE[@invalid_format]
 * </pre>
 *
 * @see AbstractTile
 */
public class WrongTile extends AbstractTile<String> {

  /** 错误信息 */
  private final String message;

  /**
   * 构造方法
   *
   * @param message 错误描述文本
   */
  public WrongTile(String message) {
    this.message = message;
  }

  /**
   * 返回错误信息
   *
   * @param value 输入值（被忽略）
   * @return 错误信息文本
   */
  @Override
  public String convert(Object value) {
    return message;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WrongTile wrongTile = (WrongTile) o;
    return Objects.equals(message, wrongTile.message);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(message);
  }
}