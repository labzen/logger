package cn.labzen.logger.spring

import cn.labzen.logger.Loggers
import org.springframework.boot.logging.LoggingSystem

internal object LabzenLoggingSystemRegistry {

  /**
   * 为了兼容 Spring Boot 的 LoggingSystem.beforeInitialize() 中使用了 Slf4j 1.x 的静态类绑定能力，
   * 造成 Caused by: java.lang.ClassNotFoundException: org.slf4j.impl.StaticLoggerBinder
   * 通过设置 Spring Boot 的固定系统属性，将 Labzen 的兼容 LoggingSystem 注入
   */
  internal fun registerLabzenLoggingSystem() {
    try {
      Class.forName("org.springframework.boot.SpringBootVersion")
    } catch (e: Exception) {
      // ignore, without spring boot
      return
    }

    if (Loggers.isLogbackPresent) {
      System.setProperty(
        LoggingSystem.SYSTEM_PROPERTY,
        "cn.labzen.logger.spring.ls.logback.LabzenLogbackLoggingSystem"
      )
    }
    if (Loggers.isReload4jPresent) {
      System.setProperty(
        LoggingSystem.SYSTEM_PROPERTY,
        "cn.labzen.logger.spring.ls.reload4j.LabzenReload4jLoggingSystem"
      )
    }
  }
}
