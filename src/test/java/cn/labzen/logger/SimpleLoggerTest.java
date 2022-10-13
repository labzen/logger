package cn.labzen.logger;

import cn.labzen.logger.kernel.LabzenLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLoggerTest {

  private static Logger logger;

  @BeforeAll
  static void init() {
    Loggers.enhance();
    logger = LoggerFactory.getLogger(SimpleLoggerTest.class);
  }

  @Test
  void test() {
    System.out.println("             >>>>>> : should be output: 'super simple text'");
    logger.debug("super simple text");

    System.out.println("             >>>>>> : should be output: 'text with parameters: 1, 2'");
    logger.debug("text with parameters: {}, {}", "1", 2);

    System.out.println("             >>>>>> : should be output: 'normal exception'");
    logger.error("a normal throwable error", new RuntimeException("just so so"));

    // ---------------------------------------------------------------------------------------------

    LabzenLogger enhancedLogger = (LabzenLogger) logger;

    System.out.println("             >>>>>> : should be output: 'text returned by function'");
    enhancedLogger.info(() -> "text returned by function");

    System.out.println("             >>>>>> : should be throw exception: 'threw runtime exception'");
    enhancedLogger.warn(new RuntimeException("threw runtime exception"));

    System.out.println("             >>>>>> : should be throw exception: 'threw runtime exception with msg'");
    System.out.println("             >>>>>> : and output: 'exception occurred'");
    enhancedLogger.error(new RuntimeException("threw runtime exception with msg"), "exception occurred");

    System.out.println(
        "             >>>>>> : should be throw exception: 'threw runtime exception with text returned by function'");
    System.out.println("             >>>>>> : and output: 'exception occurred'");
    enhancedLogger.warn(new RuntimeException("threw runtime exception with text returned by function"),
        () -> "exception occurred");

    System.out.println(
        "             >>>>>> : should be throw exception: 'threw runtime exception with text and parameters'");
    System.out.println("             >>>>>> : and output: 'exception occurred with parameters: 1, 2'");
    enhancedLogger.error(new RuntimeException("threw runtime exception with text and parameters"),
        "exception occurred with parameters: {}, {}",
        "1",
        2);
  }
}
