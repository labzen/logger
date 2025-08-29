package cn.labzen.logger.spring.ls;

import cn.labzen.logger.LoggerImplementor;
import cn.labzen.logger.spring.ls.logback.LabzenLogbackLoggingSystem;
import cn.labzen.logger.spring.ls.reload4j.LabzenReload4jLoggingSystem;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.LoggingSystemFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 为了兼容 Spring Boot 的 LoggingSystem.beforeInitialize() 中使用了 Slf4j 1.x 的静态类绑定能力，
 * 造成 Caused by: java.lang.ClassNotFoundException: org.slf4j.impl.StaticLoggerBinder
 * 通过设置 Spring Boot 的固定系统属性，将 Labzen 的兼容 LoggingSystem 注入
 * <p>
 * 设置加载顺序为最高级+512，spring官方最靠前的是LogbackLoggingSystem，它的order是最高级+1024，这样保证这个LoggingSystemFactory能最先加载处理
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 512)
public class LabzenLoggingSystemFactory implements LoggingSystemFactory {

  @Override
  public LoggingSystem getLoggingSystem(ClassLoader classLoader) {
    if (LoggerImplementor.isLogbackPresent()) {
      return new LabzenLogbackLoggingSystem(classLoader);
    }
    if (LoggerImplementor.isReload4jPresent()) {
      return new LabzenReload4jLoggingSystem(classLoader);
    }
    return null;
  }
}
