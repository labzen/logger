package cn.labzen.logger.spring.ls.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.ScanException;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import org.springframework.boot.logging.LogFile;
import org.springframework.boot.logging.logback.ColorConverter;
import org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter;
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter;

import java.nio.charset.Charset;

public class DefaultLogbackConfiguration {

  private final LogFile logFile;

  public DefaultLogbackConfiguration(LogFile logFile) {
    this.logFile = logFile;
  }

  public void apply(LogbackConfigurator configurator) {
    Object lock = configurator.getConfigurationLock();
    synchronized (lock) {
      try {
        defaults(configurator);
        Appender<ILoggingEvent> consoledAppender = consoleAppender(configurator);
        if (logFile != null) {
          Appender<ILoggingEvent> fileAppender = fileAppender(configurator, logFile.toString());
          configurator.root(Level.INFO, consoledAppender, fileAppender);
        } else {
          configurator.root(Level.INFO, consoledAppender);
        }
      } catch (ScanException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void defaults(LogbackConfigurator configurator) throws ScanException {
    configurator.conversionRule("clr", ColorConverter.class);
    configurator.conversionRule("wex", WhitespaceThrowableProxyConverter.class);
    configurator.conversionRule("wEx", ExtendedWhitespaceThrowableProxyConverter.class);
    configurator.getContext()
                .putProperty("CONSOLE_LOG_PATTERN",
                    resolve(configurator,
                        "\\${CONSOLE_LOG_PATTERN:-" +
                        "%clr(%d{\\${LOG_DATEFORMAT_PATTERN:-yyyy-MM-dd HH:mm:ss.SSS}}){faint} " +
                        "%clr(\\${LOG_LEVEL_PATTERN:-%5p}) " +
                        "%clr(\\${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} " +
                        "%clr(%-40.40logger{39}){cyan} " +
                        "%clr(:){faint} %m%n\\${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"));

    String defaultCharset = Charset.defaultCharset().name();
    configurator.getContext()
                .putProperty("CONSOLE_LOG_CHARSET",
                    resolve(configurator, "\\${CONSOLE_LOG_CHARSET:-" + defaultCharset + "}"));
    configurator.getContext()
                .putProperty("FILE_LOG_PATTERN",
                    resolve(configurator,
                        "\\${FILE_LOG_PATTERN:-" +
                        "%d{\\${LOG_DATEFORMAT_PATTERN:-yyyy-MM-dd HH:mm:ss.SSS}} \\${LOG_LEVEL_PATTERN:-%5p} \\${PID:- } --- [%t] " +
                        "%-40.40logger{39} : %m%n\\${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}"));
    configurator.getContext()
                .putProperty("FILE_LOG_CHARSET",
                    resolve(configurator, "\\${FILE_LOG_CHARSET:-" + defaultCharset + "}"));
    configurator.logger("org.apache.catalina.startup.DigesterFactory", Level.ERROR);
    configurator.logger("org.apache.catalina.util.LifecycleBase", Level.ERROR);
    configurator.logger("org.apache.coyote.http11.Http11NioProtocol", Level.WARN);
    configurator.logger("org.apache.sshd.common.util.SecurityUtils", Level.WARN);
    configurator.logger("org.apache.tomcat.util.net.NioSelectorPool", Level.WARN);
    configurator.logger("org.eclipse.jetty.util.component.AbstractLifeCycle", Level.ERROR);
    configurator.logger("org.hibernate.validator.internal.util.Version", Level.WARN);
    configurator.logger("org.springframework.boot.actuate.endpoint.jmx", Level.WARN);
  }

  private Appender<ILoggingEvent> consoleAppender(LogbackConfigurator configurator) throws ScanException {
    ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
    PatternLayoutEncoder encoder = new PatternLayoutEncoder();

    encoder.setPattern(resolve(configurator, "\\${CONSOLE_LOG_PATTERN}"));
    encoder.setCharset(resolveCharset(configurator, "\\${CONSOLE_LOG_CHARSET}"));
    configurator.start(encoder);
    appender.setEncoder(encoder);
    configurator.appender("CONSOLE", appender);
    return appender;
  }

  private Appender<ILoggingEvent> fileAppender(LogbackConfigurator configurator, String logFile) throws ScanException {
    RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setPattern(resolve(configurator, "\\${FILE_LOG_PATTERN}"));
    encoder.setCharset(resolveCharset(configurator, "\\${FILE_LOG_CHARSET}"));
    appender.setEncoder(encoder);
    configurator.start(encoder);
    appender.setFile(logFile);
    setRollingPolicy(appender, configurator);
    configurator.appender("FILE", appender);
    return appender;
  }

  private void setRollingPolicy(RollingFileAppender<ILoggingEvent> appender, LogbackConfigurator configurator) throws
      ScanException {
    SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
    rollingPolicy.setContext(configurator.getContext());
    rollingPolicy.setFileNamePattern(resolve(configurator,
        "\\${LOGBACK_ROLLINGPOLICY_FILE_NAME_PATTERN:-\\${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz}"));
    rollingPolicy.setCleanHistoryOnStart(resolveBoolean(configurator,
        "\\${LOGBACK_ROLLINGPOLICY_CLEAN_HISTORY_ON_START:-false}"));
    rollingPolicy.setMaxFileSize(resolveFileSize(configurator, "\\${LOGBACK_ROLLINGPOLICY_MAX_FILE_SIZE:-10MB}"));
    rollingPolicy.setTotalSizeCap(resolveFileSize(configurator, "\\${LOGBACK_ROLLINGPOLICY_TOTAL_SIZE_CAP:-0}"));
    rollingPolicy.setMaxHistory(resolveInt(configurator, "\\${LOGBACK_ROLLINGPOLICY_MAX_HISTORY:-7}"));
    appender.setRollingPolicy(rollingPolicy);
    rollingPolicy.setParent(appender);
    configurator.start(rollingPolicy);
  }

  private Boolean resolveBoolean(LogbackConfigurator config, String value) throws ScanException {
    return Boolean.parseBoolean(resolve(config, value));
  }

  private int resolveInt(LogbackConfigurator config, String value) throws ScanException {
    return Integer.parseInt(resolve(config, value));
  }

  private FileSize resolveFileSize(LogbackConfigurator config, String value) throws ScanException {
    return FileSize.valueOf(resolve(config, value));
  }

  private Charset resolveCharset(LogbackConfigurator config, String value) throws ScanException {
    return Charset.forName(resolve(config, value));
  }

  private String resolve(LogbackConfigurator config, String value) throws ScanException {
    return OptionHelper.substVars(value, config.getContext());
  }
}
