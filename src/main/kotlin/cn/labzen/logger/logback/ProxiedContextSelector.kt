package cn.labzen.logger.logback

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.selector.ContextSelector
import net.sf.cglib.proxy.Enhancer

@Suppress("unused")
class ProxiedContextSelector(context: LoggerContext) : ContextSelector {

  private val proxy: LoggerContext

  init {
    val enhancer = Enhancer()
    enhancer.setSuperclass(LoggerContext::class.java)
    val target = LoggerContextInterceptor(context)
    enhancer.setCallback(target)
    proxy = enhancer.create() as LoggerContext
  }

  override fun getLoggerContext() = proxy

  override fun getLoggerContext(name: String): LoggerContext? =
    if (defaultLoggerContext.name.equals(name)) {
      defaultLoggerContext
    } else {
      null
    }

  override fun getDefaultLoggerContext() = loggerContext

  override fun detachLoggerContext(loggerContextName: String) = proxy

  override fun getContextNames() =
    listOf(proxy.name)
}
