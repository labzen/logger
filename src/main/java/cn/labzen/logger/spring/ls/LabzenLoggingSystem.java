package cn.labzen.logger.spring.ls;

import org.springframework.boot.logging.AbstractLoggingSystem;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LoggingInitializationContext;

public abstract class LabzenLoggingSystem extends AbstractLoggingSystem {

  public LabzenLoggingSystem(ClassLoader classLoader) {
    super(classLoader);
  }

  @Override
  protected void loadDefaults(LoggingInitializationContext initializationContext, LogFile logFile) {
    // do nothing
  }

  @Override
  protected void loadConfiguration(LoggingInitializationContext initializationContext,
                                   String location,
                                   LogFile logFile) {
    if (initializationContext != null) {
      applySystemProperties(initializationContext.getEnvironment(), logFile);
    }
  }
}
