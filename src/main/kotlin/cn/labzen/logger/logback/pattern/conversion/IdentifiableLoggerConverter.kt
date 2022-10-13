package cn.labzen.logger.logback.pattern.conversion

import ch.qos.logback.classic.pattern.LoggerConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import cn.labzen.meta.Labzens
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * 可进一步缩写类名%logger，比如类路径已知是Spring Boot的，则显示 "SpringBoot@{类名}"
 * TODO 暂时不建议使用，未使用缓存，日志处理会比较慢
 */
class IdentifiableLoggerConverter : LoggerConverter() {

  override fun convert(event: ILoggingEvent): String {
    return loggerText(event) ?: super.convert(event)
  }

  private fun loggerText(event: ILoggingEvent): String? {
    val fnp = getFullyQualifiedName(event)
    var pn = lessClassPackagePath(fnp)
    while (pn.isNotEmpty()) {
      if (packages.containsKey(pn)) {
        return packages[pn] + classRefer(event)
      }
      pn = lessClassPackagePath(pn)
    }
    return null
  }

  private fun lessClassPackagePath(path: String) =
    path.dropLastWhile { it != '.' }.dropLast(1)

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
