package cn.labzen.logger.spring.ls.reload4j

import cn.labzen.logger.spring.ls.LabzenLoggingSystem
import org.apache.log4j.Hierarchy
import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.PropertyConfigurator
import org.apache.log4j.helpers.Loader
import org.apache.log4j.spi.Configurator
import org.apache.log4j.spi.LoggerRepository
import org.apache.log4j.xml.DOMConfigurator
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.logging.LogFile
import org.springframework.boot.logging.LogLevel
import org.springframework.boot.logging.LoggingInitializationContext
import org.springframework.util.ClassUtils
import org.springframework.util.StringUtils
import java.net.URL
import java.util.*

@Suppress("unused")
class LabzenReload4jLoggingSystem(classLoader: ClassLoader) : LabzenLoggingSystem(classLoader) {

  override fun getStandardConfigLocations(): Array<String?> {
    return getCurrentlySupportedConfigLocations()
  }

  override fun beforeInitialize() {
    val loggerRepository: LoggerRepository = getLoggerRepository()
    if (isAlreadyInitialized(loggerRepository)) {
      return
    }
    super.beforeInitialize()
  }

  override fun initialize(
    initializationContext: LoggingInitializationContext?,
    configLocation: String?,
    logFile: LogFile?
  ) {
    val loggerRepository: LoggerRepository = getLoggerRepository()
    if (isAlreadyInitialized(loggerRepository)) {
      return
    }
    super.initialize(initializationContext, configLocation, logFile)
    markAsInitialized(loggerRepository)
  }

  override fun reinitialize(initializationContext: LoggingInitializationContext) {
    val loggerRepository: LoggerRepository = getLoggerRepository()
    loggerRepository.resetConfiguration()
  }

  override fun getSupportedLogLevels(): Set<LogLevel?>? {
    return LEVELS.supported
  }

  override fun setLogLevel(loggerName: String?, logLevel: LogLevel?) {
    setLogLevel(loggerName, LEVELS.convertSystemToNative(logLevel))
  }

  private fun getCurrentlySupportedConfigLocations(): Array<String?> {
    val supportedConfigLocations: MutableList<String> = ArrayList()
    addTestFiles(supportedConfigLocations)
    supportedConfigLocations.add("log4j2.properties")
    if (isClassAvailable("com.fasterxml.jackson.dataformat.yaml.YAMLParser")) {
      Collections.addAll(supportedConfigLocations, "log4j2.yaml", "log4j2.yml")
    }
    if (isClassAvailable("com.fasterxml.jackson.databind.ObjectMapper")) {
      Collections.addAll(supportedConfigLocations, "log4j2.json", "log4j2.jsn")
    }
    supportedConfigLocations.add("log4j2.xml")
    return StringUtils.toStringArray(supportedConfigLocations)
  }

  private fun addTestFiles(supportedConfigLocations: MutableList<String>) {
    supportedConfigLocations.add("log4j2-test.properties")
    if (isClassAvailable("com.fasterxml.jackson.dataformat.yaml.YAMLParser")) {
      Collections.addAll(supportedConfigLocations, "log4j2-test.yaml", "log4j2-test.yml")
    }
    if (isClassAvailable("com.fasterxml.jackson.databind.ObjectMapper")) {
      Collections.addAll(supportedConfigLocations, "log4j2-test.json", "log4j2-test.jsn")
    }
    supportedConfigLocations.add("log4j2-test.xml")
  }

  private fun isClassAvailable(className: String?): Boolean {
    return ClassUtils.isPresent(className!!, classLoader)
  }

  private fun getOverrides(initializationContext: LoggingInitializationContext): List<String> {
    val overrides = Binder.get(initializationContext.environment)
      .bind("logging.log4j2.config.override", Bindable.listOf(String::class.java))
    return overrides.orElse(emptyList())
  }

  override fun loadConfiguration(
    initializationContext: LoggingInitializationContext?,
    location: String,
    logFile: LogFile?
  ) {
    val resource: URL = Loader.getResource(location) ?: return

    val configurator: Configurator = if (location.endsWith(".xml")) {
      DOMConfigurator()
    } else {
      PropertyConfigurator()
    }
    configurator.doConfigure(resource, getLoggerRepository())
  }

  private fun setLogLevel(loggerName: String?, level: Level?) {
    val loggerRepository = getLoggerRepository()
    val logger = loggerName?.let { loggerRepository.getLogger(it) } ?: loggerRepository.rootLogger
    logger.level = level
  }

  private fun getLoggerRepository(): LoggerRepository {
    return LogManager.getLoggerRepository()
  }

  private fun isAlreadyInitialized(loggerRepository: LoggerRepository): Boolean {
    return loggerRepository.exists(LabzenReload4jLoggingSystem::class.java.name) != null
  }

  private fun markAsInitialized(loggerRepository: LoggerRepository) {
    loggerRepository.getLogger(loggerRepository::class.java.name)
  }

  private fun markAsUninitialized(loggerRepository: LoggerRepository) {
    if (loggerRepository is Hierarchy) {
      loggerRepository.clear()
    }
  }

  companion object {

    private val LEVELS = LogLevels<Level>().also {
      it.map(LogLevel.TRACE, Level.TRACE)
      it.map(LogLevel.DEBUG, Level.DEBUG)
      it.map(LogLevel.INFO, Level.INFO)
      it.map(LogLevel.WARN, Level.WARN)
      it.map(LogLevel.ERROR, Level.ERROR)
      it.map(LogLevel.FATAL, Level.FATAL)
      it.map(LogLevel.OFF, Level.OFF)
    }
  }
}
