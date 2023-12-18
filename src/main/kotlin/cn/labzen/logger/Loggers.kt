package cn.labzen.logger

import cn.labzen.logger.kernel.LabzenLogger
import cn.labzen.logger.spring.SpringLoggingSystemRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Method
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
    // 探测项目实际使用的 Log 门面实现框架，优先使用 Logback
    detectLoggerImplements()

    // 通过 javassist 字节码类编辑，将 Labzen Logger 的功能切入进去，必须在第一次创建 Logger 变量之前调用，否则会异常
    // ServiceProviderReflectiveProcessor.reflective()

    // 针对 SLF4J 的 2.0.9 以后的版本，提供了 slf4j.provider 系统属性：显式指定provider类，这绕过了查找提供者的服务加载器机制，并可缩短SLF4J的初始化时间。
    // 提供定制的 provider 来增强 Logback 或 Reload4j 的功能
    setSlf4jServiceProvider()

    // 隐藏因为 javassist 包做字节码操作产生的警告信息；可能放在这不是很合适，以后移出去
    disableIllegalReflectiveWarning()

    // 为了兼容 Spring Boot 的 LoggingSystem.beforeInitialize() 中使用了 Slf4j 1.x 的静态类绑定能力，
    // 造成 Caused by: java.lang.ClassNotFoundException: org.slf4j.impl.StaticLoggerBinder
    // 通过设置 Spring Boot 的固定系统属性，将 Labzen 的兼容 LoggingSystem 注入
    SpringLoggingSystemRegistry.registerLabzenLoggingSystem()
  }

  private fun detectLoggerImplements() {
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

  private fun setSlf4jServiceProvider() {
    if (isLogbackPresent) {
      System.setProperty(
        LoggerFactory.PROVIDER_PROPERTY_KEY,
        "cn.labzen.logger.logback.LabzenLogbackServiceProvider"
      )
    }
    if (isReload4jPresent) {
      System.setProperty(
        LoggerFactory.PROVIDER_PROPERTY_KEY,
        "cn.labzen.logger.reload4j.LabzenReload4jServiceProvider"
      )
    }
  }

  /**
   * 忽略非法反射警告 适用于jdk11
   *
   * 实际输出的警告为：
   * WARNING: An illegal reflective access operation has occurred
   * WARNING: Illegal reflective access by javassist.util.proxy.SecurityActions (file:/Users/dean/.m2/repository/org/javassist/javassist/3.28.0-GA/javassist-3.28.0-GA.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)
   * WARNING: Please consider reporting this to the maintainers of javassist.util.proxy.SecurityActions
   * WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
   * WARNING: All illegal access operations will be denied in a future release
   */
  private fun disableIllegalReflectiveWarning() {
    try {
      val unsafeClass = Unsafe::class.java
      val theUnsafeField: Field = unsafeClass.getDeclaredField("theUnsafe")
      val putObjectVolatileMethod: Method = unsafeClass.getDeclaredMethod(
        "putObjectVolatile", Object::class.java,
        Long::class.java, Object::class.java
      )
      val staticFieldOffsetMethod = unsafeClass.getDeclaredMethod("staticFieldOffset", Field::class.java)
      theUnsafeField.isAccessible = true
      val u: Unsafe = theUnsafeField.get(null) as Unsafe
      val loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger")
      val loggerField: Field = loggerClass.getDeclaredField("logger")
      val offset = staticFieldOffsetMethod.invoke(u, loggerField) as Long
      putObjectVolatileMethod.invoke(u, loggerClass, offset, null)
    } catch (e: Exception) {
      // ignore
    }
  }

  @JvmStatic
  fun getLogger(name: String): LabzenLogger =
    LoggerFactory.getLogger(name) as LabzenLogger

  @JvmStatic
  fun getLogger(clazz: Class<*>): LabzenLogger =
    LoggerFactory.getLogger(clazz) as LabzenLogger
}
