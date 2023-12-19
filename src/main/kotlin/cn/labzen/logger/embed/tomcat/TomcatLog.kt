package cn.labzen.logger.embed.tomcat

import cn.labzen.logger.Loggers
import org.apache.juli.logging.Log

class TomcatLog(fqcn: String) : Log {

  constructor() : this("_UNKNOWN_")

  private val logger = Loggers.getLogger(fqcn)

  companion object {
    private val SERVLET_NAME = "TOMCAT"
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
    if (t == null) {
      logger.trace("{}", message)
    } else {
      logger.trace().scene(SERVLET_NAME).setCause(t).log("$message")
    }
  }

  override fun debug(message: Any?) {
    logger.debug().scene(SERVLET_NAME).log("$message")
  }

  override fun debug(message: Any?, t: Throwable?) {
    if (t == null) {
      logger.debug("{}", message)
    } else {
      logger.debug().scene(SERVLET_NAME).setCause(t).log("$message")
    }
  }

  override fun info(message: Any?) {
    logger.info().scene(SERVLET_NAME).log("$message")
  }

  override fun info(message: Any?, t: Throwable?) {
    if (t == null) {
      logger.info("{}", message)
    } else {
      logger.info().scene(SERVLET_NAME).setCause(t).log("$message")
    }
  }

  override fun warn(message: Any?) {
    logger.warn().scene(SERVLET_NAME).log("$message")
  }

  override fun warn(message: Any?, t: Throwable?) {
    if (t == null) {
      logger.warn("{}", message)
    } else {
      logger.warn().scene(SERVLET_NAME).setCause(t).log("$message")
    }
  }

  override fun error(message: Any?) {
    logger.error().scene(SERVLET_NAME).log("$message")
  }

  override fun error(message: Any?, t: Throwable?) {
    if (t == null) {
      logger.error("{}", message)
    } else {
      logger.error().scene(SERVLET_NAME).setCause(t).log("$message")
    }
  }

  override fun fatal(message: Any?) {
    logger.error().scene(SERVLET_NAME).log("$message")
  }

  override fun fatal(message: Any?, t: Throwable?) {
    if (t == null) {
      logger.error("{}", message)
    } else {
      logger.error().scene(SERVLET_NAME).setCause(t).log("$message")
    }
  }
}
