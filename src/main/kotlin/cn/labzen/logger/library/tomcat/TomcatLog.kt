package cn.labzen.logger.library.tomcat

import cn.labzen.logger.Loggers
import org.apache.juli.logging.Log

class TomcatLog(fqcn: String) : Log {

  constructor() : this("_UNKNOWN_")

  private val logger = Loggers.getLogger(fqcn)

  companion object {
    private const val SERVLET_NAME = "TOMCAT"
  }

  override fun isDebugEnabled(): Boolean =
    logger.isDebugEnabled

  override fun isErrorEnabled(): Boolean =
    logger.isErrorEnabled

  override fun isFatalEnabled(): Boolean =
    logger.isErrorEnabled

  override fun isInfoEnabled(): Boolean =
    logger.isInfoEnabled

  override fun isTraceEnabled(): Boolean =
    logger.isTraceEnabled

  override fun isWarnEnabled(): Boolean =
    logger.isWarnEnabled

  override fun trace(message: Any?) {
    logger.trace().scene(SERVLET_NAME).log("$message")
  }

  override fun trace(message: Any?, t: Throwable?) {
    logger.trace().scene(SERVLET_NAME).setCause(t).log("$message")
  }

  override fun debug(message: Any?) {
    logger.debug().scene(SERVLET_NAME).log("$message")
  }

  override fun debug(message: Any?, t: Throwable?) {
    logger.debug().scene(SERVLET_NAME).setCause(t).log("$message")
  }

  override fun info(message: Any?) {
    logger.info().scene(SERVLET_NAME).log("$message")
  }

  override fun info(message: Any?, t: Throwable?) {
    logger.info().scene(SERVLET_NAME).setCause(t).log("$message")
  }

  override fun warn(message: Any?) {
    logger.warn().scene(SERVLET_NAME).log("$message")
  }

  override fun warn(message: Any?, t: Throwable?) {
    logger.warn().scene(SERVLET_NAME).setCause(t).log("$message")
  }

  override fun error(message: Any?) {
    logger.error().scene(SERVLET_NAME).log("$message")
  }

  override fun error(message: Any?, t: Throwable?) {
    logger.error().scene(SERVLET_NAME).setCause(t).log("$message")
  }

  override fun fatal(message: Any?) {
    logger.error().scene(SERVLET_NAME).log("$message")
  }

  override fun fatal(message: Any?, t: Throwable?) {
    logger.error().scene(SERVLET_NAME).setCause(t).log("$message")
  }
}
