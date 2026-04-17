package cn.labzen.logger.kernel.marker;

import ch.qos.logback.core.util.StringUtil;

/**
 * 状态Marker，用于标识日志相关的状态信息。
 *
 * <p>用途：
 * <ul>
 *   <li>标识操作的成功/失败状态</li>
 *   <li>标识需要关注的警告等级</li>
 *   <li>配合{@link cn.labzen.logger.kernel.enums.Status}枚举使用</li>
 * </ul>
 *
 * <p>输出格式：直接输出文本，null转为空字符串
 *
 * @see AbstractLimitedMarker
 * @see cn.labzen.logger.kernel.enums.Status
 */
public class StatusMarker extends AbstractLimitedMarker {

  /** 状态文本 */
  private final String text;

  /**
   * 构造方法
   *
   * @param text 状态文本，null会被转为空字符串
   */
  public StatusMarker(String text) {
    this.text = text;
  }

  /**
   * 返回状态文本
   *
   * <p>null安全：null转为空字符串
   *
   * @return 状态文本
   */
  @Override
  public String toString() {
    return StringUtil.nullStringToEmpty(text);
  }
}