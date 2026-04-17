package cn.labzen.logger;

import cn.labzen.logger.kernel.provider.LabzenLoggerServiceProvider;
import org.springframework.util.ClassUtils;

/**
 * 日志框架检测器，探测项目使用的底层日志实现。
 *
 * <p>支持的日志框架：
 * <ul>
 *   <li>Logback - 优先检测，ch.qos.logback.classic</li>
 *   <li>Reload4j - 备选检测，org.slf4j.reload4j</li>
 * </ul>
 *
 * <p>使用方式：
 * <ul>
 *   <li>在ServiceProvider构造时调用{@link #detect()}</li>
 *   <li>通过静态方法查询检测结果</li>
 * </ul>
 *
 * @see LabzenLoggerServiceProvider
 */
public class LoggerImplementor {

  /** Logback是否存在标志 */
  private static boolean logbackPresent = false;

  /** Reload4j是否存在标志 */
  private static boolean reload4jPresent = false;

  /**
   * 私有构造方法，防止实例化
   */
  private LoggerImplementor() {
  }

  /**
   * 探测项目实际使用的日志框架
   *
   * <p>使用ClassUtils.isPresent检测类路径中是否存在对应的Provider
   * <p>优先级：Logback > Reload4j
   */
  public static void detect() {
    logbackPresent = ClassUtils.isPresent("ch.qos.logback.classic.spi.LogbackServiceProvider",
        LoggerImplementor.class.getClassLoader());

    reload4jPresent = ClassUtils.isPresent("org.slf4j.reload4j.Reload4jServiceProvider",
        LoggerImplementor.class.getClassLoader());
  }

  /**
   * 检查Logback是否可用
   *
   * @return true表示Logback存在
   */
  public static boolean isLogbackPresent() {
    return logbackPresent;
  }

  /**
   * 检查Reload4j是否可用
   *
   * @return true表示Reload4j存在
   */
  public static boolean isReload4jPresent() {
    return reload4jPresent;
  }
}
