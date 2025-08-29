package cn.labzen.logger.spring.ls.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.InfoStatus;

public class DebugLogbackConfigurator extends LogbackConfigurator {

  public DebugLogbackConfigurator(LoggerContext context) {
    super(context);
  }

  @Override
  public void conversionRule(String conversionWord, Class<? extends Converter<?>> converterClass) {
    info("Adding conversion rule of type '" + converterClass.getName() + "' for word '" + conversionWord + "'");
    super.conversionRule(conversionWord, converterClass);
  }

  @Override
  public void appender(String name, Appender<?> appender) {
    info("Adding appender '" + appender + "' named '" + name + "'");
    super.appender(name, appender);
  }

  @Override
  public void logger(String name, Level level, boolean additive, Appender<ILoggingEvent> appender) {
    info("Configuring logger '" + name + "' with level '" + level + "'. Additive: " + additive);
    if (appender != null) {
      info("Adding appender '" + appender + "' to logger '" + name + "'");
    }
    super.logger(name, level, additive, appender);
  }

  @Override
  public void start(LifeCycle lifeCycle) {
    info("Starting '" + lifeCycle + "'");
    super.start(lifeCycle);
  }

  private void info(String message) {
    getContext().getStatusManager().add(new InfoStatus(message, this));
  }
}
