package cn.labzen.logger.logback

import ch.qos.logback.classic.LoggerContext
import cn.labzen.logger.kernel.LabzenLogger
import cn.labzen.logger.logback.filter.ForcedFilter
import cn.labzen.logger.logback.pattern.conversion.IdentifiableLoggerConverter
import org.slf4j.ILoggerFactory
import org.slf4j.Logger

@Suppress("unused")
class LabzenLogbackLoggerContext(private val principal: LoggerContext) : ILoggerFactory {

  init {
    instance = this

    principal.addTurboFilter(ForcedFilter())
    IdentifiableLoggerConverter.collectLabzenComponentPackages()
  }

  internal fun getContext() = principal

  override fun getLogger(name: String): Logger {
    val original: Logger = principal.getLogger(name)
    return LabzenLogger(original)
  }

  companion object {

    private var instance: LabzenLogbackLoggerContext? = null

    internal fun singleton(): LabzenLogbackLoggerContext {
      return instance!!
    }
  }
}
