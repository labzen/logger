package cn.labzen.logger.logback.spi

import ch.qos.logback.classic.ClassicConstants
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.Configurator
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.encoder.LayoutWrappingEncoder
import ch.qos.logback.core.spi.ContextAwareBase
import cn.labzen.logger.config.ConfigurationLoader
import cn.labzen.logger.config.LabzenLoggerConfiguration
import cn.labzen.logger.logback.ProxiedContextSelector
import cn.labzen.logger.logback.layout.EnhancedLayout
import sun.misc.Unsafe
import java.lang.reflect.Field

class LogbackConfigurator : ContextAwareBase(), Configurator {

  override fun configure(loggerContext: LoggerContext) {
    disableWarning()
    ConfigurationLoader.loadOneByOne()

    // 借助 ContextSelector 的实现，来切入 LoggerFactory 的植入
    System.setProperty(ClassicConstants.LOGBACK_CONTEXT_SELECTOR, ProxiedContextSelector::class.java.name)

    addInfo("Setting Up Labzen Console Log Configuration.")

    val ca = ConsoleAppender<ILoggingEvent>()
    ca.context = loggerContext
    ca.name = "console"
    val encoder = LayoutWrappingEncoder<ILoggingEvent>()
    encoder.context = loggerContext

    val layout = EnhancedLayout()

    layout.context = loggerContext
    layout.start()
    encoder.layout = layout

    ca.encoder = encoder
    ca.start()

    val rootLogger: Logger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME)
    rootLogger.level = Level.toLevel(LabzenLoggerConfiguration.instance.rootLevel, Level.INFO)
    rootLogger.addAppender(ca)
  }

  private fun disableWarning() {
    try {
      val theUnsafe: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
      theUnsafe.isAccessible = true
      val u: Unsafe = theUnsafe.get(null) as Unsafe
      val cls = Class.forName("jdk.internal.module.IllegalAccessLogger")
      val logger: Field = cls.getDeclaredField("logger")
      u.putObjectVolatile(cls, u.staticFieldOffset(logger), null)
    } catch (e: Exception) {
      // ignore
    }
  }
}
