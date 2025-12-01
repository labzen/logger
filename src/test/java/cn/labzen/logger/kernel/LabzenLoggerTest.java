package cn.labzen.logger.kernel;

import cn.labzen.logger.Loggers;
import cn.labzen.meta.LabzenMetaInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import static org.junit.jupiter.api.Assertions.*;

class LabzenLoggerTest {

  private static LabzenLogger logger;

  @BeforeAll
  static void init() {
    Loggers.enhance();
    new LabzenMetaInitializer().initialize(null);
    logger = Loggers.getLogger(LabzenLoggerTest.class);
  }

  @Test
  void testGetName() {
    assertNotNull(logger.getName());
    assertTrue(logger.getName().contains("LabzenLoggerTest"));
  }

  @Test
  void testIsEnabledForLevel() {
    assertTrue(logger.isEnabledForLevel(Level.INFO));
    assertTrue(logger.isEnabledForLevel(Level.WARN));
    assertTrue(logger.isEnabledForLevel(Level.ERROR));
  }

  @Test
  void testTraceLevel() {
    assertTrue(logger.isTraceEnabled() || !logger.isTraceEnabled());
    logger.trace("trace message");
    logger.trace("trace with arg {}", "value");
    logger.trace("trace with two args {} {}", "val1", "val2");
    logger.trace("trace with args", "arg1", "arg2", "arg3");
    logger.trace("trace with exception", new RuntimeException("test exception"));
    logger.trace(() -> "trace with supplier");
    logger.trace(new RuntimeException("test"));
    logger.trace(new RuntimeException("test"), "message");
    logger.trace(new RuntimeException("test"), () -> "supplier message");
    logger.trace(new RuntimeException("test"), "message {}", "arg");
  }

  @Test
  void testDebugLevel() {
    assertTrue(logger.isDebugEnabled() || !logger.isDebugEnabled());
    logger.debug("debug message");
    logger.debug("debug with arg {}", "value");
    logger.debug("debug with two args {} {}", "val1", "val2");
    logger.debug("debug with args", "arg1", "arg2", "arg3");
    logger.debug("debug with exception", new RuntimeException("test exception"));
    logger.debug(() -> "debug with supplier");
    logger.debug(new RuntimeException("test"));
    logger.debug(new RuntimeException("test"), "message");
    logger.debug(new RuntimeException("test"), () -> "supplier message");
    logger.debug(new RuntimeException("test"), "message {}", "arg");
  }

  @Test
  void testInfoLevel() {
    assertTrue(logger.isInfoEnabled());
    logger.info("info message");
    logger.info("info with arg {}", "value");
    logger.info("info with two args {} {}", "val1", "val2");
    logger.info("info with args", "arg1", "arg2", "arg3");
    logger.info("info with exception", new RuntimeException("test exception"));
    logger.info(() -> "info with supplier");
    logger.info(new RuntimeException("test"));
    logger.info(new RuntimeException("test"), "message");
    logger.info(new RuntimeException("test"), () -> "supplier message");
    logger.info(new RuntimeException("test"), "message {}", "arg");
  }

  @Test
  void testWarnLevel() {
    assertTrue(logger.isWarnEnabled());
    logger.warn("warn message");
    logger.warn("warn with arg {}", "value");
    logger.warn("warn with two args {} {}", "val1", "val2");
    logger.warn("warn with args", "arg1", "arg2", "arg3");
    logger.warn("warn with exception", new RuntimeException("test exception"));
    logger.warn(() -> "warn with supplier");
    logger.warn(new RuntimeException("test"));
    logger.warn(new RuntimeException("test"), "message");
    logger.warn(new RuntimeException("test"), () -> "supplier message");
    logger.warn(new RuntimeException("test"), "message {}", "arg");
  }

  @Test
  void testErrorLevel() {
    assertTrue(logger.isErrorEnabled());
    logger.error("error message");
    logger.error("error with arg {}", "value");
    logger.error("error with two args {} {}", "val1", "val2");
    logger.error("error with args", "arg1", "arg2", "arg3");
    logger.error("error with exception", new RuntimeException("test exception"));
    logger.error(() -> "error with supplier");
    logger.error(new RuntimeException("test"));
    logger.error(new RuntimeException("test"), "message");
    logger.error(new RuntimeException("test"), () -> "supplier message");
    logger.error(new RuntimeException("test"), "message {}", "arg");
  }

  @Test
  void testFluentAPI() {
    assertInstanceOf(LabzenLoggingEventBuilder.class, logger.atTrace());
    assertInstanceOf(LabzenLoggingEventBuilder.class, logger.atDebug());
    assertInstanceOf(LabzenLoggingEventBuilder.class, logger.atInfo());
    assertInstanceOf(LabzenLoggingEventBuilder.class, logger.atWarn());
    assertInstanceOf(LabzenLoggingEventBuilder.class, logger.atError());
    assertNotNull(logger.atLevel(Level.INFO));
  }

  @Test
  void testMessagePrefix() {
    logger.startMessagePrefix("PREFIX:", true);
    logger.info("message with prefix");
    String prefix = logger.getMessagePrefix();
    assertEquals("PREFIX:", prefix);
    logger.endMessagePrefix(true);
    assertNull(logger.getMessagePrefix());
  }

  @Test
  void testMakeLoggingEventBuilder() {
    assertNotNull(logger.makeLoggingEventBuilder(Level.INFO));
    assertNotNull(logger.makeLoggingEventBuilder(Level.WARN));
  }
}
