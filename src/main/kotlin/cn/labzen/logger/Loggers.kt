package cn.labzen.logger

import cn.labzen.logger.kernel.LabzenLogger
import cn.labzen.logger.kernel.ServiceProviderReflectiveProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 获取 Logger 实例，工厂类（等同于 Slf4j 的 [LoggerFactory]），用于替代 Slf4j 的工厂类，直接获取增强后的 Logger；
 *
 * 不使用本工厂类，而使用 Lombok 的 @Slf4j 注解，或使用 [LoggerFactory] 获取的 Slf4j [Logger]接口，其具体实现为 [LabzenLogger]
 */
object Loggers {

  /**
   * 增强SLF4j，改方法必须在第一次使用[LoggerFactory.getLogger]调用之前（包括使用 Lombok 的 @Slf4j 等注解获取Logger对象的方式）执行
   */
  @JvmStatic
  fun enhance() {
    ServiceProviderReflectiveProcessor.reflective()
  }

  @JvmStatic
  fun getLogger(name: String): LabzenLogger =
    LoggerFactory.getLogger(name) as LabzenLogger

  @JvmStatic
  fun getLogger(clazz: Class<*>): LabzenLogger =
    LoggerFactory.getLogger(clazz) as LabzenLogger
}
