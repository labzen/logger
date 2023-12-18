package cn.labzen.logger.spring.ls.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.turbo.TurboFilter
import ch.qos.logback.classic.util.ContextInitializer
import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.spi.FilterReply
import ch.qos.logback.core.status.OnConsoleStatusListener
import ch.qos.logback.core.status.Status
import ch.qos.logback.core.util.StatusListenerConfigHelper
import cn.labzen.logger.logback.LabzenLogbackLoggerContext
import cn.labzen.logger.spring.ls.LabzenLoggingSystem
import org.slf4j.Marker
import org.springframework.boot.logging.*
import org.springframework.boot.logging.logback.LogbackLoggingSystem
import org.springframework.boot.logging.logback.LogbackLoggingSystemProperties
import org.springframework.core.SpringProperties
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.util.ResourceUtils
import org.springframework.util.StringUtils
import java.net.URL

@Suppress("unused")
class LabzenLogbackLoggingSystem(classLoader: ClassLoader) : LabzenLoggingSystem(classLoader) {

  override fun getStandardConfigLocations(): Array<String> =
    arrayOf("logback-test.groovy", "logback-test.xml", "logback.groovy", "logback.xml")


  override fun beforeInitialize() {
    val loggerContext = getLoggerContext()
    if (isAlreadyInitialized(loggerContext)) {
      return
    }
    super.beforeInitialize()
    loggerContext.turboFilterList.add(FILTER)
  }

  override fun getSystemProperties(environment: ConfigurableEnvironment?): LoggingSystemProperties =
    LogbackLoggingSystemProperties(environment)

  override fun initialize(
    initializationContext: LoggingInitializationContext?,
    configLocation: String?,
    logFile: LogFile?
  ) {
    val loggerContext: LoggerContext = getLoggerContext()
    if (isAlreadyInitialized(loggerContext)) {
      return
    }
    super.initialize(initializationContext, configLocation, logFile)
    loggerContext.turboFilterList.remove(FILTER)
    markAsInitialized(loggerContext)
    if (StringUtils.hasText(System.getProperty(CONFIGURATION_FILE_PROPERTY))) {
      val logger = loggerContext.getLogger(LogbackLoggingSystem::class.java.name)
      logger.warn(
        "Ignoring '$CONFIGURATION_FILE_PROPERTY' system property. Please use 'logging.config' instead."
      )
    }
  }

  override fun loadDefaults(initializationContext: LoggingInitializationContext, logFile: LogFile?) {
    val context = getLoggerContext()
    stopAndReset(context)
    val debug = java.lang.Boolean.getBoolean("logback.debug")
    if (debug) {
      StatusListenerConfigHelper.addOnConsoleListenerInstance(context, OnConsoleStatusListener())
    }
    val environment = initializationContext.environment
    // Apply system properties directly in case the same JVM runs multiple apps
    LogbackLoggingSystemProperties(
      environment
    ) { key: String?, `val`: String? ->
      context.putProperty(
        key,
        `val`
      )
    }.apply(logFile)

    val configurator = if (debug) DebugLogbackConfigurator(context) else LogbackConfigurator(context)
    DefaultLogbackConfiguration(logFile).apply(configurator)
    context.isPackagingDataEnabled = true
  }

  override fun loadConfiguration(
    initializationContext: LoggingInitializationContext?, location: String,
    logFile: LogFile?
  ) {
    super.loadConfiguration(initializationContext, location, logFile)
    val loggerContext = getLoggerContext()
    stopAndReset(loggerContext)
    try {
      configureByResourceUrl(initializationContext, loggerContext, ResourceUtils.getURL(location))
    } catch (ex: Exception) {
      throw IllegalStateException("Could not initialize Logback logging from $location", ex)
    }
    val statuses = loggerContext.statusManager.copyOfStatusList
    val errors = StringBuilder()
    for (status in statuses) {
      if (status.level == Status.ERROR) {
        errors.append(if (errors.isNotEmpty()) String.format("%n") else "")
        errors.append(status.toString())
      }
    }
    check(errors.isEmpty()) { String.format("Logback configuration error detected: %n%s", errors) }
  }

  override fun getShutdownHandler(): Runnable =
    Runnable {
      getLoggerContext().stop()
    }


  override fun cleanUp() {
    val context = getLoggerContext()
    markAsUninitialized(context)
    super.cleanUp()
    context.statusManager.clear()
    context.turboFilterList.remove(FILTER)
  }

  override fun reinitialize(initializationContext: LoggingInitializationContext?) {
    getLoggerContext().reset()
    getLoggerContext().statusManager.clear()
    loadConfiguration(initializationContext, selfInitializationConfig, null)
  }

  override fun getLoggerConfigurations(): List<LoggerConfiguration?> {
    val result: MutableList<LoggerConfiguration?> = ArrayList()
    for (logger in getLoggerContext().loggerList) {
      result.add(getLoggerConfiguration(logger))
    }
    result.sortWith(CONFIGURATION_COMPARATOR)
    return result
  }

  override fun getLoggerConfiguration(loggerName: String?): LoggerConfiguration? {
    val name: String = getLoggerName(loggerName)
    val loggerContext = getLoggerContext()
    return getLoggerConfiguration(loggerContext.exists(name))
  }

  override fun getSupportedLogLevels(): Set<LogLevel?>? {
    return LEVELS.supported
  }

  override fun setLogLevel(loggerName: String?, level: LogLevel?) {
    val logger: Logger = getLoggerContext().getLogger(loggerName)
    logger.level = LEVELS.convertSystemToNative(level)
  }


  private fun getLoggerConfiguration(logger: Logger?): LoggerConfiguration? {
    if (logger == null) {
      return null
    }
    val level = LEVELS.convertNativeToSystem(logger.level)
    val effectiveLevel = LEVELS.convertNativeToSystem(logger.effectiveLevel)
    val name = getLoggerName(logger.name)
    return LoggerConfiguration(name, level, effectiveLevel)
  }

  private fun getLoggerName(name: String?): String {
    return if (!StringUtils.hasLength(name) || org.slf4j.Logger.ROOT_LOGGER_NAME == name) {
      ROOT_LOGGER_NAME
    } else name!!
  }

  private fun stopAndReset(loggerContext: LoggerContext) {
    loggerContext.stop()
    loggerContext.reset()
  }

  @Throws(JoranException::class)
  private fun configureByResourceUrl(
    initializationContext: LoggingInitializationContext?, loggerContext: LoggerContext,
    url: URL
  ) {
    if (XML_ENABLED && url.toString().endsWith("xml")) {
      val sbClass = Class.forName("org.springframework.boot.logging.logback.SpringBootJoranConfigurator")
      val sbConstructor = sbClass.getDeclaredConstructor(LoggingInitializationContext::class.java)
      sbConstructor.isAccessible = true
      val configurator = sbConstructor.newInstance(initializationContext) as JoranConfigurator
      // val configurator: JoranConfigurator = SpringBootJoranConfigurator(initializationContext)
      configurator.context = loggerContext
      configurator.doConfigure(url)
    } else {
      ContextInitializer(loggerContext).autoConfig(classLoader)
    }
  }

  private fun getLoggerContext() =
    LabzenLogbackLoggerContext.instance.getContext()

  private fun isAlreadyInitialized(loggerContext: LoggerContext): Boolean =
    loggerContext.getObject(LoggingSystem::class.java.name) != null

  private fun markAsInitialized(loggerContext: LoggerContext) {
    loggerContext.putObject(LoggingSystem::class.java.name, Any())
  }

  private fun markAsUninitialized(loggerContext: LoggerContext) {
    loggerContext.removeObject(LoggingSystem::class.java.name)
  }

  companion object {
    private val XML_ENABLED = !SpringProperties.getFlag("spring.xml.ignore")
    private const val CONFIGURATION_FILE_PROPERTY = "logback.configurationFile"

    private val LEVELS = LogLevels<Level>().also {
      it.map(LogLevel.TRACE, Level.TRACE)
      it.map(LogLevel.DEBUG, Level.DEBUG)
      it.map(LogLevel.INFO, Level.INFO)
      it.map(LogLevel.WARN, Level.WARN)
      it.map(LogLevel.FATAL, Level.ERROR)
      it.map(LogLevel.ERROR, Level.ERROR)
      it.map(LogLevel.OFF, Level.OFF)
    }

    var FILTER: TurboFilter = object : TurboFilter() {
      override fun decide(
        marker: Marker?, logger: Logger?, level: Level?, format: String?,
        params: Array<Any?>?, t: Throwable?
      ): FilterReply {
        return FilterReply.DENY
      }
    }
  }
}
