package cn.labzen.logger.logback

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import cn.labzen.logger.core.EnhancedLogger
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import java.lang.reflect.Method

class LoggerContextInterceptor(private val context: LoggerContext) : MethodInterceptor {

  override fun intercept(target: Any, method: Method, arguments: Array<out Any>, methodProxy: MethodProxy): Any? {
    if (method.name == "getLogger") {
      val arg = arguments[0]
      val name: String = if (arg is Class<*>) arg.name else arg as String
      val logger = method.invoke(context, name) as Logger
      return EnhancedLogger(logger)
    }

    return methodProxy.invokeSuper(target, arguments)
  }
}
