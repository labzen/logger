package cn.labzen.logger.kernel.marker;

/**
 * 场景Marker，用于标识日志所属的业务场景。
 *
 * <p>常见场景：
 * <ul>
 *   <li>LOGIN/LOGOUT - 登录登出</li>
 *   <li>REQUEST/RESPONSE - 请求响应</li>
 *   <li>TASK/JOB - 定时任务</li>
 *   <li>FILTER/INTERCEPTOR - 过滤器拦截器</li>
 * </ul>
 *
 * <p>输出格式：{@code <SCENE>}
 *
 * @see AbstractLimitedMarker
 * @see cn.labzen.logger.kernel.enums.Scenes
 */
public class SceneMarker extends AbstractLimitedMarker {

  /** 场景文本 */
  private final String text;

  /**
   * 构造方法
   *
   * @param text 场景标识文本
   */
  public SceneMarker(String text) {
    this.text = text;
  }

  /**
   * 返回带尖括号包裹的场景文本
   *
   * @return 格式化后的文本，如"CONTROLLER" → "&lt;CONTROLLER&gt;"
   */
  @Override
  public String toString() {
    return "<" + text + ">";
  }
}