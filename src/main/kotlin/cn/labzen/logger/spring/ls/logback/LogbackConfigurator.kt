package cn.labzen.logger.spring.ls.logback

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.pattern.Converter
import ch.qos.logback.core.spi.ContextAware
import ch.qos.logback.core.spi.LifeCycle
import org.slf4j.Logger
import org.springframework.util.Assert

open class LogbackConfigurator(private val context: LoggerContext) {

  fun getContext(): LoggerContext {
    return context
  }

  fun getConfigurationLock(): Any? {
    return context.configurationLock
  }

  open fun conversionRule(conversionWord: String, converterClass: Class<out Converter<*>>) {
    Assert.hasLength(conversionWord, "Conversion word must not be empty")
    Assert.notNull(converterClass, "Converter class must not be null")
    val obj: Any? = context.getObject(CoreConstants.PATTERN_RULE_REGISTRY)

    @Suppress("UNCHECKED_CAST")
    val registry: MutableMap<String, String> = obj?.let {
      it as HashMap<String, String>
    } ?: run {
      HashMap<String, String>().also {
        context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, it)
      }
    }
    registry[conversionWord] = converterClass.name
  }

  open fun appender(name: String, appender: Appender<*>) {
    appender.name = name
    start(appender)
  }

  fun logger(name: String, level: Level?) {
    logger(name, level, true)
  }

  fun logger(name: String, level: Level?, additive: Boolean) {
    logger(name, level, additive, null)
  }

  open fun logger(name: String, level: Level?, additive: Boolean, appender: Appender<ILoggingEvent?>?) {
    val logger = context.getLogger(name)
    if (level != null) {
      logger.level = level
    }
    logger.isAdditive = additive
    if (appender != null) {
      logger.addAppender(appender)
    }
  }

  @SafeVarargs
  fun root(level: Level?, vararg appenders: Appender<ILoggingEvent>?) {
    val logger = context.getLogger(Logger.ROOT_LOGGER_NAME)
    if (level != null) {
      logger.level = level
    }
    for (appender in appenders) {
      logger.addAppender(appender)
    }
  }

  open fun start(lifeCycle: LifeCycle) {
    if (lifeCycle is ContextAware) {
      (lifeCycle as ContextAware).context = context
    }
    lifeCycle.start()
  }
}
