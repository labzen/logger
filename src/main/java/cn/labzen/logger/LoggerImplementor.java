package cn.labzen.logger;

import org.springframework.util.ClassUtils;

public class LoggerImplementor {

  private static boolean logbackPresent = false;
  private static boolean reload4jPresent = false;

  private LoggerImplementor() {
  }

  /**
   * 探测项目实际使用的 Log 门面实现框架，优先使用 Logback
   */
  public static void detect() {
    logbackPresent = ClassUtils.isPresent("ch.qos.logback.classic.spi.LogbackServiceProvider",
        LoggerImplementor.class.getClassLoader());

    reload4jPresent = ClassUtils.isPresent("org.slf4j.reload4j.Reload4jServiceProvider",
        LoggerImplementor.class.getClassLoader());
  }

  public static boolean isLogbackPresent() {
    return logbackPresent;
  }

  public static boolean isReload4jPresent() {
    return reload4jPresent;
  }
}
