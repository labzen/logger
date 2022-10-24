package cn.labzen.logger

import cn.labzen.logger.kernel.LabzenLogger
import cn.labzen.logger.kernel.ServiceProviderReflectiveProcessor
import cn.labzen.logger.spring.LabzenLoggingSystemRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

/**
 * 获取 Logger 实例，工厂类（等同于 Slf4j 的 [LoggerFactory]），用于替代 Slf4j 的工厂类，直接获取增强后的 Logger；
 *
 * 不使用本工厂类，而使用 Lombok 的 @Slf4j 注解，或使用 [LoggerFactory] 获取的 Slf4j [Logger]接口，其具体实现为 [LabzenLogger]
 */
object Loggers {

  internal var isLogbackPresent by Delegates.notNull<Boolean>()
  internal var isReload4jPresent by Delegates.notNull<Boolean>()


  /**
   * 增强SLF4j，改方法必须在第一次使用[LoggerFactory.getLogger]调用之前（包括使用 Lombok 的 @Slf4j 等注解获取Logger对象的方式）执行
   */
  @JvmStatic
  fun enhance() {
    // 通过 javassist 字节码类编辑，将 Labzen Logger 的功能切入进去，必须在第一次创建 Logger 变量之前调用，否则会异常
    ServiceProviderReflectiveProcessor.reflective()

    discover()

    // 为了兼容 Spring Boot 的 LoggingSystem.beforeInitialize() 中使用了 Slf4j 1.x 的静态类绑定能力，
    // 造成 Caused by: java.lang.ClassNotFoundException: org.slf4j.impl.StaticLoggerBinder
    // 通过设置 Spring Boot 的固定系统属性，将 Labzen 的兼容 LoggingSystem 注入
    LabzenLoggingSystemRegistry.registerLabzenLoggingSystem()
  }

  private fun discover() {
    isLogbackPresent = try {
      Class.forName("ch.qos.logback.classic.spi.LogbackServiceProvider")
      true
    } catch (e: Exception) {
      false
    }
    isReload4jPresent = try {
      Class.forName("org.slf4j.reload4j.Reload4jServiceProvider")
      true
    } catch (e: Exception) {
      false
    }
  }

  @JvmStatic
  fun getLogger(name: String): LabzenLogger =
    LoggerFactory.getLogger(name) as LabzenLogger

  @JvmStatic
  fun getLogger(clazz: Class<*>): LabzenLogger =
    LoggerFactory.getLogger(clazz) as LabzenLogger
}
