package cn.labzen.logger.logback.pattern.converter

import ch.qos.logback.classic.pattern.Abbreviator
import ch.qos.logback.classic.pattern.ClassNameOnlyAbbreviator
import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator
import ch.qos.logback.classic.spi.ILoggingEvent

class IdentifiableLoggerConverter : ClassicConverter() {

  private var abbr: Abbreviator? = null

  override fun start() {
    val optStr = firstOption
    if (optStr != null) {
      try {
        val targetLen = optStr.toInt()
        if (targetLen == 0) {
          abbr = ClassNameOnlyAbbreviator()
        } else if (targetLen > 0) {
          abbr = TargetLengthBasedClassNameAbbreviator(targetLen)
        }
      } catch (nfe: NumberFormatException) {
        // FIXME: better error reporting
      }
    }
  }

  override fun convert(event: ILoggingEvent): String =
    loggerText(event.loggerName)?.let {
      it + classRefer(event)
    } ?: event.loggerName.let {
      if (abbr == null) {
        it
      } else {
        abbr!!.abbreviate(it)
      }
    }

  private fun loggerText(loggerName: String): String? {
    // TODO 如果使用indexOf+substring代替split+take(i)，性能是否会提高？
    val parts = loggerName.split('.')

    for (i in parts.size - 1 downTo 0) {
      val subPackage = parts.take(i).joinToString(".")
      if (packages.containsKey(subPackage)) {
        return packages[subPackage]
      }
    }
    return null
  }

  private fun classRefer(event: ILoggingEvent) =
    event.callerData.find {
      it.className == event.loggerName
    }?.let { "(${it.fileName}:${it.lineNumber})" } ?: event.loggerName.substringAfterLast('.')

  companion object {
    // TODO 丰富各jar包的路径，通过SPI？
    private val packages = mutableMapOf<String, String>().apply {
      this["cn.labzen.cells.core"] = "Labzen-Cells.Core@"
      this["cn.labzen.cells.network"] = "Labzen-Cells.Network@"
      this["cn.labzen.cells.algorithm"] = "Labzen-Cells.Algorithm@"
      this["cn.labzen.logger"] = "Labzen-Logger@"

      this["org.springframework.boot"] = "SpringBoot@"
      this["org.springframework.boot.actuate"] = "SpringBoot-Actuate@"
    }.toMap()
  }
}
