package cn.labzen.logger.spring.ls

import org.springframework.boot.logging.AbstractLoggingSystem
import org.springframework.boot.logging.LogFile
import org.springframework.boot.logging.LoggingInitializationContext

abstract class LabzenLoggingSystem(classLoader: ClassLoader) : AbstractLoggingSystem(classLoader) {

  override fun loadDefaults(initializationContext: LoggingInitializationContext, logFile: LogFile?) {
    // do nothing
  }

  override fun loadConfiguration(
    initializationContext: LoggingInitializationContext?,
    location: String,
    logFile: LogFile?
  ) {
    if (initializationContext != null) {
      applySystemProperties(initializationContext.environment, logFile)
    }
  }
}
