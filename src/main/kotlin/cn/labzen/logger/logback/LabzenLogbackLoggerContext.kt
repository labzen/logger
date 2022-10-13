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

  init {
    // 加入自定义 Conversion Word
    // 重新分配色彩的日志级别
    PatternLayout.DEFAULT_CONVERTER_MAP["ZLevel"] = ColoredLevelConverter::class.java.name
    // 更短的logger类显示，不建议使用
    PatternLayout.DEFAULT_CONVERTER_MAP["ZLogger"] = IdentifiableLoggerConverter::class.java.name
    // 更直观的异常显示
    PatternLayout.DEFAULT_CONVERTER_MAP["ZException"] = IndentedThrowableProxyConverter::class.java.name
  }

  override fun getLogger(name: String): Logger {
    val original: Logger = principal.getLogger(name)
    return LabzenLogger(original)
  }
}
