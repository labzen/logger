package cn.labzen.logger.kernel

import javassist.ClassPool
import javassist.CtField
import javassist.Modifier
import javassist.NotFoundException
import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 反射修改SLF4j提供者（logback, reload4j）的初始化过程，以实现labzen logger的增强注入
 */
internal object ServiceProviderReflectiveProcessor {

  fun reflective() {
    disableWarning()

    processLogback()
  }

  private fun processLogback() {
    val pool = ClassPool.getDefault()

    val providerClass = try {
      pool.get("ch.qos.logback.classic.spi.LogbackServiceProvider")
    } catch (e: NotFoundException) {
      return
    }

    val loggerContextClass = pool.get("cn.labzen.logger.logback.LabzenLogbackLoggerContext")
    val loggerContextField = CtField(loggerContextClass, "labzenLoggerContext", providerClass)
    loggerContextField.modifiers = Modifier.PRIVATE
    providerClass.addField(loggerContextField)

    val initializeMethod = providerClass.getDeclaredMethod("initialize")
    initializeMethod.insertAt(
      40,
      "labzenLoggerContext = new cn.labzen.logger.logback.LabzenLogbackLoggerContext(defaultLoggerContext);"
    )

    val getLoggerFactoryMethod = providerClass.getDeclaredMethod("getLoggerFactory")
    getLoggerFactoryMethod.setBody("return labzenLoggerContext;")

    providerClass.toClass()
    providerClass.writeFile()
  }

  /**
   * 忽略非法反射警告 适用于jdk11
   */
  private fun disableWarning() {
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
}
