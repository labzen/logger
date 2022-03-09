package cn.labzen.logger;

import cn.labzen.logger.core.EnhancedLogger;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SimpleLoggerTest {

  @Test
  void test() {
    System.out.println("             >>>>>> : should be output: 'super simple text'");
    log.debug("super simple text");

    System.out.println("             >>>>>> : should be output: 'text with parameters: 1, 2'");
    log.debug("text with parameters: {}, {}", "1", 2);

    System.out.println("             >>>>>> : should be output: 'normal exception'");
    log.error("a normal throwable error", new RuntimeException("just so so"));

    // ---------------------------------------------------------------------------------------------

    EnhancedLogger enhancedLogger = (EnhancedLogger) log;

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

    System.out.println("             >>>>>> : should be throw exception: 'threw runtime exception with text and parameters'");
    System.out.println("             >>>>>> : and output: 'exception occurred with parameters: 1, 2'");
    enhancedLogger.error(new RuntimeException("threw runtime exception with text and parameters"),
        "exception occurred with parameters: {}, {}",
        "1",
        2);
  }
}
