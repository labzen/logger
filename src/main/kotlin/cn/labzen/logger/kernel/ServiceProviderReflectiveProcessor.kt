// package cn.labzen.logger.kernel
//
// import javassist.ClassPool
// import javassist.CtField
// import javassist.Modifier
// import javassist.NotFoundException
// import sun.misc.Unsafe
// import java.lang.reflect.Field
// import java.lang.reflect.Method
//
// /**
//  * 反射修改SLF4j提供者（logback, reload4j）的初始化过程，以实现labzen logger的增强注入
//  */
// @Deprecated("")
// internal object ServiceProviderReflectiveProcessor {
//
//   fun reflective() {
//     disableWarning()
//
//     processLogback()
//     processReload4j()
//   }
//
//   private fun processLogback() {
//     val pool = ClassPool.getDefault()
//
//     val providerClass = try {
//       pool.get("ch.qos.logback.classic.spi.LogbackServiceProvider")
//     } catch (e: NotFoundException) {
//       return
//     }
//
//     val loggerContextClass = pool.get("cn.labzen.logger.logback.LabzenLogbackLoggerContext")
//     val loggerContextField = CtField(loggerContextClass, "labzenLoggerContext", providerClass)
//     loggerContextField.modifiers = Modifier.PRIVATE
//     providerClass.addField(loggerContextField)
//
//     val initializeMethod = providerClass.getDeclaredMethod("initialize")
//     initializeMethod.insertBefore("cn.labzen.logger.logback.LogbackPreprocessor.process();")
//     initializeMethod.insertAfter("labzenLoggerContext = new cn.labzen.logger.logback.LabzenLogbackLoggerContext(defaultLoggerContext);")
//
//     val getLoggerFactoryMethod = providerClass.getDeclaredMethod("getLoggerFactory")
//     getLoggerFactoryMethod.setBody("return labzenLoggerContext;")
//
//     providerClass.toClass()
//   }
//
//   private fun processReload4j() {
//     val pool = ClassPool.getDefault()
//
//     val providerClass = try {
//       pool.get("org.slf4j.reload4j.Reload4jServiceProvider")
//     } catch (e: NotFoundException) {
//       return
//     }
//
//     val loggerFactoryClass = pool.get("cn.labzen.logger.reload4j.LabzenReload4jLoggerFactory")
//     val loggerFactoryField = CtField(loggerFactoryClass, "labzenLoggerFactory", providerClass)
//     loggerFactoryField.modifiers = Modifier.PRIVATE
//     providerClass.addField(loggerFactoryField)
//
//     val initializeMethod = providerClass.getDeclaredMethod("initialize")
//     initializeMethod.insertAfter("labzenLoggerFactory = new cn.labzen.logger.reload4j.LabzenReload4jLoggerFactory(loggerFactory);")
//
//     val getLoggerFactoryMethod = providerClass.getDeclaredMethod("getLoggerFactory")
//     getLoggerFactoryMethod.setBody("return labzenLoggerFactory;")
//
//     providerClass.toClass()
//   }
//
//   /**
//    * 忽略非法反射警告 适用于jdk11
//    */
//   private fun disableWarning() {
//     try {
//       val unsafeClass = Unsafe::class.java
//       val theUnsafeField: Field = unsafeClass.getDeclaredField("theUnsafe")
//       val putObjectVolatileMethod: Method = unsafeClass.getDeclaredMethod(
//         "putObjectVolatile", Object::class.java,
//         Long::class.java, Object::class.java
//       )
//       val staticFieldOffsetMethod = unsafeClass.getDeclaredMethod("staticFieldOffset", Field::class.java)
//
//       theUnsafeField.isAccessible = true
//       val u: Unsafe = theUnsafeField.get(null) as Unsafe
//
//       val loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger")
//       val loggerField: Field = loggerClass.getDeclaredField("logger")
//       val offset = staticFieldOffsetMethod.invoke(u, loggerField) as Long
//       putObjectVolatileMethod.invoke(u, loggerClass, offset, null)
//     } catch (e: Exception) {
//       // ignore
//     }
//   }
// }
