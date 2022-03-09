package cn.labzen.logger

import cn.labzen.logger.core.EnhancedLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 获取 Logger 实例，工厂类（等同于 Slf4j 的 [LoggerFactory]），用于替代 Slf4j 的工厂类，直接获取增强后的 Logger；
 *
 * 不使用本工厂类，而使用 Lombok 的 @Slf4j 注解，或使用 [LoggerFactory] 获取的 Slf4j [Logger]接口，其具体实现为 [EnhancedLogger]
 */
object Loggers {

  @JvmStatic
  fun getLogger(name: String): EnhancedLogger =
    LoggerFactory.getLogger(name) as EnhancedLogger

  @JvmStatic
  fun getLogger(clazz: Class<*>): EnhancedLogger =
    LoggerFactory.getLogger(clazz) as EnhancedLogger
}
