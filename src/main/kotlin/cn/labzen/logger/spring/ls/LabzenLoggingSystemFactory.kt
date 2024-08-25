package cn.labzen.logger.spring.ls

import cn.labzen.logger.Loggers
import cn.labzen.logger.spring.ls.logback.LabzenLogbackLoggingSystem
import cn.labzen.logger.spring.ls.reload4j.LabzenReload4jLoggingSystem
import org.springframework.boot.logging.LoggingSystem
import org.springframework.boot.logging.LoggingSystemFactory


/**
 * 为了兼容 Spring Boot 的 LoggingSystem.beforeInitialize() 中使用了 Slf4j 1.x 的静态类绑定能力，
 * 造成 Caused by: java.lang.ClassNotFoundException: org.slf4j.impl.StaticLoggerBinder
 * 通过设置 Spring Boot 的固定系统属性，将 Labzen 的兼容 LoggingSystem 注入
 */
class LabzenLoggingSystemFactory : LoggingSystemFactory {

  override fun getLoggingSystem(classLoader: ClassLoader): LoggingSystem? {
    if (Loggers.isLogbackPresent) {
      return LabzenLogbackLoggingSystem(classLoader)
    }
    if (Loggers.isReload4jPresent) {
      return LabzenReload4jLoggingSystem(classLoader)
    }
    return null
  }
}
