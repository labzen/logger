package cn.labzen.logger.logback

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.spi.LogbackServiceProvider
import cn.labzen.logger.logback.pattern.conversion.HighlighterConverter
import cn.labzen.logger.logback.pattern.conversion.IdentifiableLoggerConverter
import cn.labzen.logger.logback.pattern.conversion.IndentedThrowableProxyConverter
import org.slf4j.ILoggerFactory

class LabzenLogbackServiceProvider : LogbackServiceProvider() {

  private lateinit var labzenLoggerContext: ILoggerFactory

  override fun initialize() {
    enhancePatternLayout()

    super.initialize()

    val principalContext = super.getLoggerFactory() as LoggerContext
    labzenLoggerContext = LabzenLogbackLoggerContext(principalContext)
  }

  override fun getLoggerFactory(): ILoggerFactory =
    labzenLoggerContext

  private fun enhancePatternLayout() {
    // 加入自定义 Conversion Word
    // 重新分配色彩的日志级别
    PatternLayout.DEFAULT_CONVERTER_MAP["showy"] = HighlighterConverter::class.java.name
    PatternLayout.DEFAULT_CONVERTER_MAP["highlighter"] = HighlighterConverter::class.java.name
    // 更短的logger类显示，不建议使用
    PatternLayout.DEFAULT_CONVERTER_MAP["briefLogger"] = IdentifiableLoggerConverter::class.java.name
    PatternLayout.DEFAULT_CONVERTER_MAP["brief"] = IdentifiableLoggerConverter::class.java.name
    PatternLayout.DEFAULT_CONVERTER_MAP["bl"] = IdentifiableLoggerConverter::class.java.name
    // 更直观的异常显示
    PatternLayout.DEFAULT_CONVERTER_MAP["thrown"] = IndentedThrowableProxyConverter::class.java.name
    PatternLayout.DEFAULT_CONVERTER_MAP["newEx"] = IndentedThrowableProxyConverter::class.java.name
    PatternLayout.DEFAULT_CONVERTER_MAP["newException"] = IndentedThrowableProxyConverter::class.java.name
  }

}
