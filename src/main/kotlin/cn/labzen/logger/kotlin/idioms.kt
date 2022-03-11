package cn.labzen.logger.kotlin

import cn.labzen.logger.Loggers
import java.lang.reflect.Modifier

fun logger(func: () -> Unit) = Loggers.getLogger(namingFunc(func))

fun logger(classType: Class<*>) = Loggers.getLogger(namingType(classType))

fun logger(className: String) = Loggers.getLogger(className)

private fun namingFunc(func: () -> Unit): String {
  val name = func.javaClass.name
  return when {
    name.contains("Kt$") -> name.substringBefore("Kt$")
    name.contains("$") -> name.substringBefore("$")
    else -> name
  }
}

private fun <T : Any> namingType(forClass: Class<T>): String = unwrapCompanionClass(forClass).name

private fun <T : Any> unwrapCompanionClass(cls: Class<T>): Class<*> {
  if (cls.enclosingClass != null) {
    try {
      val field = cls.enclosingClass.getField(cls.simpleName)
      if (Modifier.isStatic(field.modifiers) && field.type == cls) {
        // && field.get(null) === obj
        // the above might be safer but problematic with initialization order
        return cls.enclosingClass
      }
    } catch (e: Exception) {
      // 不是companion object
    }
  }
  return cls
}
