package cn.labzen.logger.spring.ls.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.util.StatusListenerConfigHelper;
import cn.labzen.logger.logback.LabzenLogbackLoggerContext;
import cn.labzen.logger.spring.ls.LabzenLoggingSystem;
import org.slf4j.Marker;
import org.springframework.boot.logging.*;
import org.springframework.boot.logging.logback.LogbackLoggingSystemProperties;
import org.springframework.core.SpringProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LabzenLogbackLoggingSystem extends LabzenLoggingSystem {

  private static final boolean XML_ENABLED = !SpringProperties.getFlag("spring.xml.ignore");
  private static final String CONFIGURATION_FILE_PROPERTY = "logback.configurationFile";
  private static final LogLevels<Level> LEVELS;
  private static final TurboFilter FILTER = new TurboFilter() {
    @Override
    public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
      return FilterReply.DENY;
    }
  };

  static {
    LEVELS = new LogLevels<>();
    LEVELS.map(LogLevel.TRACE, Level.TRACE);
    LEVELS.map(LogLevel.DEBUG, Level.DEBUG);
    LEVELS.map(LogLevel.INFO, Level.INFO);
    LEVELS.map(LogLevel.WARN, Level.WARN);
    LEVELS.map(LogLevel.ERROR, Level.ERROR);
    LEVELS.map(LogLevel.FATAL, Level.ERROR);
    LEVELS.map(LogLevel.OFF, Level.OFF);
  }

  public LabzenLogbackLoggingSystem(ClassLoader classLoader) {
    super(classLoader);
  }

  @Override
  protected String[] getStandardConfigLocations() {
    return new String[]{"logback-test.groovy", "logback-test.xml", "logback.groovy", "logback.xml"};
  }

  @Override
  public void beforeInitialize() {
    LoggerContext loggerContext = loggerContext();
    if (alreadyInitialized(loggerContext)) {
      return;
    }
    super.beforeInitialize();
    loggerContext.addTurboFilter(FILTER);
  }

  @Override
  public LoggingSystemProperties getSystemProperties(ConfigurableEnvironment environment) {
    return new LogbackLoggingSystemProperties(environment);
  }

  @Override
  public void initialize(LoggingInitializationContext initializationContext, String configLocation, LogFile logFile) {
    LoggerContext loggerContext = loggerContext();
    if (alreadyInitialized(loggerContext)) {
      return;
    }

    super.initialize(initializationContext, configLocation, logFile);
    loggerContext.getTurboFilterList().remove(FILTER);
    makeLoggerContextInitialized();
    if (StringUtils.hasText(System.getProperty(CONFIGURATION_FILE_PROPERTY))) {
      Logger logger = loggerContext.getLogger(LabzenLogbackLoggingSystem.class);
      logger.warn("Ignoring '" +
                  CONFIGURATION_FILE_PROPERTY +
                  "' system property. Please use 'logging.config' instead.");
    }
  }

  public void loadDefaults(LoggingInitializationContext initializationContext, LogFile logFile) {
    LoggerContext loggerContext = loggerContext();
    stopAndReset(loggerContext);
    boolean debug = Boolean.getBoolean("logback.debug");
    if (debug) {
      StatusListenerConfigHelper.addOnConsoleListenerInstance(loggerContext, new OnConsoleStatusListener());
    }

    Environment environment = initializationContext.getEnvironment();
    // Apply system properties directly in case the same JVM runs multiple apps
    LogbackLoggingSystemProperties loggingSystemProperties = new LogbackLoggingSystemProperties(environment,
        loggerContext::putProperty);
    loggingSystemProperties.apply(logFile);

    LogbackConfigurator configurator;
    if (debug) {
      configurator = new DebugLogbackConfigurator(loggerContext);
    } else {
      configurator = new LogbackConfigurator(loggerContext);
    }
    DefaultLogbackConfiguration logbackConfiguration = new DefaultLogbackConfiguration(logFile);
    logbackConfiguration.apply(configurator);
    loggerContext.setPackagingDataEnabled(true);
  }

  @Override
  protected void loadConfiguration(LoggingInitializationContext initializationContext,
                                   String location,
                                   LogFile logFile) {
    super.loadConfiguration(initializationContext, location, logFile);
    LoggerContext loggerContext = loggerContext();
    stopAndReset(loggerContext);

    try {
      configureByResourceUrl(loggerContext, ResourceUtils.getURL(location));
    } catch (Exception e) {
      throw new IllegalStateException("Could not initialize Logback logging from " + location, e);
    }

    List<Status> statuses = loggerContext.getStatusManager().getCopyOfStatusList();
    StringBuilder errors = new StringBuilder();
    for (Status status : statuses) {
      if (status.getLevel() == Status.ERROR) {
        if (!errors.isEmpty()) {
          errors.append("%n");
        }
        errors.append(status);
      }
    }

    if (!errors.isEmpty()) {
      String formatted = String.format("Logback configuration error detected: %n%s", errors);
      System.out.println(formatted);
    }
  }

  @Override
  public Runnable getShutdownHandler() {
    return () -> loggerContext().stop();
  }

  @Override
  public void cleanUp() {
    makeLoggerContextUninitialized();

    super.cleanUp();
    LoggerContext loggerContext = loggerContext();
    loggerContext.getStatusManager().clear();
    loggerContext.getTurboFilterList().remove(FILTER);
  }

  @Override
  protected void reinitialize(LoggingInitializationContext initializationContext) {
    LoggerContext loggerContext = loggerContext();
    loggerContext.reset();
    loggerContext.getStatusManager().clear();
    loadConfiguration(initializationContext, super.getSelfInitializationConfig(), null);
  }

  @Override
  public List<LoggerConfiguration> getLoggerConfigurations() {
    List<LoggerConfiguration> result = new ArrayList<>();
    for (ch.qos.logback.classic.Logger logger : loggerContext().getLoggerList()) {
      result.add(getLoggerConfiguration(logger));
    }
    result.sort(CONFIGURATION_COMPARATOR);
    return result;
  }

  @Override
  public LoggerConfiguration getLoggerConfiguration(String loggerName) {
    String name = getLoggerName(loggerName);
    LoggerContext loggerContext = loggerContext();
    return getLoggerConfiguration(loggerContext.exists(name));
  }

  @Override
  public Set<LogLevel> getSupportedLogLevels() {
    return LEVELS.getSupported();
  }

  @Override
  public void setLogLevel(String loggerName, LogLevel level) {
    Logger logger = loggerContext().getLogger(loggerName);
    logger.setLevel(LEVELS.convertSystemToNative(level));
  }

  private LoggerContext loggerContext() {
    return LabzenLogbackLoggerContext.instance().getContext();
  }

  private boolean alreadyInitialized(LoggerContext loggerContext) {
    return loggerContext.getObject(LoggingSystem.class.getName()) != null;
  }

  private void makeLoggerContextInitialized() {
    LoggerContext loggerContext = loggerContext();
    loggerContext.putObject(LoggingSystem.class.getName(), new Object());
  }

  private void makeLoggerContextUninitialized() {
    LoggerContext loggerContext = loggerContext();
    loggerContext.removeObject(LoggingSystem.class.getName());
  }

  private void stopAndReset(LoggerContext loggerContext) {
    loggerContext.stop();
    loggerContext.reset();
  }

  private void configureByResourceUrl(LoggerContext loggerContext, URL url) throws
      JoranException,
      ReflectiveOperationException {
    if (XML_ENABLED && url.toString().endsWith("xml")) {
      Class<?> configuratorClass = ClassUtils.forName("ch.qos.logback.classic.joran.JoranConfigurator",
          getClassLoader());
      Constructor<?> declaredConstructor = configuratorClass.getDeclaredConstructor();
      declaredConstructor.setAccessible(true);
      JoranConfigurator configurator = (JoranConfigurator) declaredConstructor.newInstance();
      configurator.setContext(loggerContext);
      configurator.doConfigure(url);
    } else {
      ContextInitializer contextInitializer = new ContextInitializer(loggerContext);
      contextInitializer.autoConfig(getClassLoader());
    }
  }

  private LoggerConfiguration getLoggerConfiguration(Logger logger) {
    if (logger == null) {
      return null;
    }

    LogLevel level = LEVELS.convertNativeToSystem(logger.getLevel());
    LogLevel effectiveLevel = LEVELS.convertNativeToSystem(logger.getEffectiveLevel());
    String name = getLoggerName(logger.getName());
    return new LoggerConfiguration(name, level, effectiveLevel);
  }

  private String getLoggerName(String name) {
    if (!StringUtils.hasLength(name) || org.slf4j.Logger.ROOT_LOGGER_NAME.equals(name)) {
      return ROOT_LOGGER_NAME;
    }
    return name;
  }
}
