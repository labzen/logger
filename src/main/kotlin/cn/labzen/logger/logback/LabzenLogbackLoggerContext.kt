package cn.labzen.logger.logback

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.PatternLayout
import cn.labzen.logger.kernel.LabzenLogger
import cn.labzen.logger.logback.pattern.conversion.ColoredLevelConverter
import cn.labzen.logger.logback.pattern.conversion.IdentifiableLoggerConverter
import cn.labzen.logger.logback.pattern.conversion.IndentedThrowableProxyConverter
import org.slf4j.ILoggerFactory
import org.slf4j.Logger

@Suppress("unused")
class LabzenLogbackLoggerContext(private val principal: LoggerContext) : ILoggerFactory {

  override fun getLogger(name: String): Logger {
    val original: Logger = principal.getLogger(name)
    return LabzenLogger(original)
  }
}
