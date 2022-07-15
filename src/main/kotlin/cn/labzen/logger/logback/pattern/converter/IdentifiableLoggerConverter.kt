package cn.labzen.logger.logback.pattern.converter

import ch.qos.logback.classic.pattern.Abbreviator
import ch.qos.logback.classic.pattern.ClassNameOnlyAbbreviator
import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.pattern.TargetLengthBasedClassNameAbbreviator
import ch.qos.logback.classic.spi.ILoggingEvent
import cn.labzen.meta.Labzens
import java.io.IOException
import java.io.InputStream
import java.util.*

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
    // val parts = loggerName.split('.')

    var pn = loggerName.dropLastWhile { it != '.' }.dropLast(1)
    while (pn.isNotEmpty()) {
      if (packages.containsKey(pn)) {
        return packages[pn]
      }
      pn = pn.dropLastWhile { it != '.' }.dropLast(1)
    }
    return null
  }

  private fun classRefer(event: ILoggingEvent) =
    event.callerData.find {
      it.className == event.loggerName
    }?.let { "(${it.fileName}:${it.lineNumber})" } ?: event.loggerName.substringAfterLast('.')

  companion object {
    private const val LOGGER_NAMES_PROPERTIES_FILE = "logger-names.properties"
    private val packages = preparePackages()

    private fun preparePackages(): MutableMap<String, String> {
      val resource: InputStream? = Companion::class.java.classLoader.getResource(LOGGER_NAMES_PROPERTIES_FILE)?.let {
        try {
          it.openStream()
        } catch (e: IOException) {
          null
        }
      } ?: Companion::class.java.getResourceAsStream(LOGGER_NAMES_PROPERTIES_FILE)

      resource ?: return packages

      val properties = Properties().apply {
        load(resource)
      }
      val packages = mutableMapOf<String, String>()
      properties.stringPropertyNames().forEach {
        packages[it] = properties.getProperty(it)
      }

      return packages
    }

    internal fun collectLabzenComponentPackages() {
      val componentPackages = Labzens.components().values.associate {
        Pair(it.meta.instance.packageBased(), it.meta.instance.mark() + "@")
      }
      packages.putAll(componentPackages)
    }
  }
}
