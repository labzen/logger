package cn.labzen.logger.spring.ls.reload4j;

import cn.labzen.logger.spring.ls.LabzenLoggingSystem;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingInitializationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LabzenReload4jLoggingSystem extends LabzenLoggingSystem {

  private static final LogLevels<Level> LEVELS;

  static {
    LEVELS = new LogLevels<>();
    LEVELS.map(LogLevel.TRACE, Level.TRACE);
    LEVELS.map(LogLevel.DEBUG, Level.DEBUG);
    LEVELS.map(LogLevel.INFO, Level.INFO);
    LEVELS.map(LogLevel.WARN, Level.WARN);
    LEVELS.map(LogLevel.ERROR, Level.ERROR);
    LEVELS.map(LogLevel.FATAL, Level.FATAL);
    LEVELS.map(LogLevel.OFF, Level.OFF);
  }

  public LabzenReload4jLoggingSystem(ClassLoader classLoader) {
    super(classLoader);
  }

  @Override
  protected String[] getStandardConfigLocations() {
    return StringUtils.toStringArray(getCurrentlySupportedConfigLocations());
  }

  @Override
  public void beforeInitialize() {
    if (loggerRepositoryAlreadyInitialized()) {
      return;
    }
    super.beforeInitialize();
  }

  @Override
  public void initialize(LoggingInitializationContext initializationContext, String configLocation, LogFile logFile) {
    if (loggerRepositoryAlreadyInitialized()) {
      return;
    }
    super.initialize(initializationContext, configLocation, logFile);
    makeLoggerRepositoryInitialized();
  }

  @Override
  protected void reinitialize(LoggingInitializationContext initializationContext) {
    LoggerRepository loggerRepository = LogManager.getLoggerRepository();
    loggerRepository.resetConfiguration();
  }

  @Override
  public Set<LogLevel> getSupportedLogLevels() {
    return LEVELS.getSupported();
  }

  @Override
  public void setLogLevel(String loggerName, LogLevel level) {
    setLogLevel(loggerName, LEVELS.convertSystemToNative(level));
  }


  private List<String> getCurrentlySupportedConfigLocations() {
    List<String> locations = new ArrayList<>();
    addTestFiles(locations);
    locations.add("log4j2.properties");
    if (isClassAvailable("com.fasterxml.jackson.dataformat.yaml.YAMLParser")) {
      Collections.addAll(locations, "log4j2.yaml", "log4j2.yml");
    }
    if (isClassAvailable("com.fasterxml.jackson.databind.ObjectMapper")) {
      Collections.addAll(locations, "log4j2.json", "log4j2.jsn");
    }
    locations.add("log4j2.xml");
    return locations;
  }

  private void addTestFiles(List<String> supportedConfigLocations) {
    supportedConfigLocations.add("log4j2-test.properties");
    if (isClassAvailable("com.fasterxml.jackson.dataformat.yaml.YAMLParser")) {
      Collections.addAll(supportedConfigLocations, "log4j2-test.yaml", "log4j2-test.yml");
    }
    if (isClassAvailable("com.fasterxml.jackson.databind.ObjectMapper")) {
      Collections.addAll(supportedConfigLocations, "log4j2-test.json", "log4j2-test.jsn");
    }
    supportedConfigLocations.add("log4j2-test.xml");
  }

  private boolean isClassAvailable(String className) {
    return ClassUtils.isPresent(className, getClassLoader());
  }

  private boolean loggerRepositoryAlreadyInitialized() {
    LoggerRepository loggerRepository = LogManager.getLoggerRepository();
    return loggerRepository.exists(LabzenReload4jLoggingSystem.class.getName()) != null;
  }

  private void makeLoggerRepositoryInitialized() {
    LoggerRepository loggerRepository = LogManager.getLoggerRepository();
    loggerRepository.getLogger(LabzenReload4jLoggingSystem.class.getName());
  }

  private void setLogLevel(String loggerName, Level level) {
    LoggerRepository loggerRepository = LogManager.getLoggerRepository();
    Logger logger;
    if (StringUtils.hasText(loggerName)) {
      logger = loggerRepository.getRootLogger();
    } else {
      logger = loggerRepository.getLogger(loggerName);
    }
    logger.setLevel(level);
  }
}
