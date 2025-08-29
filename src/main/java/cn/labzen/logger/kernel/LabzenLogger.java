package cn.labzen.logger.kernel;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.KeyValuePair;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LoggingEventAware;
import org.slf4j.spi.LoggingEventBuilder;

import java.util.function.Supplier;

public class LabzenLogger implements Logger, LoggingEventAware {

  private final Logger principal;
  private String messagePrefix;
  private boolean messagePrefixEnabled = false;
  private boolean changeMessagePrefixAfter = false;

  public LabzenLogger(Logger principal) {
    this.principal = principal;
  }

  public void startMessagePrefix(String prefix, boolean immediately) {
    messagePrefix = prefix;
    messagePrefixEnabled = immediately;
    changeMessagePrefixAfter = !immediately;
  }

  public void endMessagePrefix(boolean immediately) {
    messagePrefix = null;
    messagePrefixEnabled = !immediately;
    changeMessagePrefixAfter = !immediately;
  }

  public String getMessagePrefix() {
    return messagePrefix;
  }

  public void log(LoggingEvent event) {
    String preprocessedMessage = mergeMarkersAndKeyValuePairs(event, event.getMessage());

    if (changeMessagePrefixAfter) {
      changeMessagePrefixAfter = false;
      messagePrefixEnabled = !messagePrefixEnabled;
    }

    boolean hasException = event.getThrowable() != null;

    switch (event.getLevel()) {
      case INFO -> {
        if (hasException) {
          principal.info(preprocessedMessage, event.getThrowable());
        } else {
          principal.info(preprocessedMessage);
        }
      }
      case WARN -> {
        if (hasException) {
          principal.warn(preprocessedMessage, event.getThrowable());
        } else {
          principal.warn(preprocessedMessage);
        }
      }
      case ERROR -> {
        if (hasException) {
          principal.error(preprocessedMessage, event.getThrowable());
        } else {
          principal.error(preprocessedMessage);
        }
      }
      case DEBUG -> {
        if (hasException) {
          principal.debug(preprocessedMessage, event.getThrowable());
        } else {
          principal.debug(preprocessedMessage);
        }
      }
      case TRACE -> {
        if (hasException) {
          principal.trace(preprocessedMessage, event.getThrowable());
        } else {
          principal.trace(preprocessedMessage);
        }
      }
      case null -> {
        if (hasException) {
          principal.info(preprocessedMessage, event.getThrowable());
        } else {
          principal.info(preprocessedMessage);
        }
      }
    }
  }

  /**
   * Prepend markers and key-value pairs to the message.
   */
  private String mergeMarkersAndKeyValuePairs(LoggingEvent event, String message) {
    StringBuilder sb = new StringBuilder();

    if (event.getMarkers() != null) {
      for (Marker marker : event.getMarkers()) {
        sb.append(marker).append(" ");
      }
    }

    sb.append(MessageFormatter.arrayFormat(message, event.getArgumentArray()).getMessage());

    if (event.getKeyValuePairs() != null) {
      for (KeyValuePair pair : event.getKeyValuePairs()) {
        sb.append(pair.key).append("=").append(pair.value).append(" ");
      }
    }

    return sb.toString();
  }

  // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ENHANCE TRADITIONAL API >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  @Override
  public String getName() {
    return principal.getName();
  }

  @Override
  public boolean isEnabledForLevel(Level level) {
    return principal.isEnabledForLevel(level);
  }

  @Override
  public LoggingEventBuilder makeLoggingEventBuilder(Level level) {
    return principal.makeLoggingEventBuilder(level);
  }

  // ==================================== trace level ======================================

  @Override
  public boolean isTraceEnabled() {
    return principal.isTraceEnabled();
  }

  @Override
  public void trace(String msg) {
    principal.trace(msg);
  }

  /**
   * trace级别日志打印
   *
   * @param supplier 获取日志内容函数
   */
  public void trace(Supplier<String> supplier) {
    principal.trace(supplier.get());
  }

  @Override
  public void trace(String format, Object arg) {
    principal.trace(format, arg);
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    principal.trace(format, arg1, arg2);
  }

  @Override
  public void trace(String format, Object... arguments) {
    principal.trace(format, arguments);
  }

  @Override
  public void trace(String msg, Throwable t) {
    principal.trace(msg, t);
  }

  /**
   * trace级别日志打印
   *
   * @param t 需要输入的异常日志内容
   */
  public void trace(Throwable t) {
    principal.trace("", t);
  }

  /**
   * trace级别日志打印
   *
   * @param t   需要输入的异常日志内容
   * @param msg 日志内容
   */
  public void trace(Throwable t, String msg) {
    principal.trace(msg, t);
  }

  /**
   * trace级别日志打印
   *
   * @param t        需要输入的异常日志内容
   * @param supplier 获取日志内容函数
   */
  public void trace(Throwable t, Supplier<String> supplier) {
    principal.trace(supplier.get(), t);
  }

  /**
   * trace级别日志打印
   *
   * @param t    需要输入的异常日志内容
   * @param msg  日志内容
   * @param args 日志参数
   */
  public void trace(Throwable t, String msg, Object... args) {
    principal.trace(MessageFormatter.arrayFormat(msg, args).getMessage(), t);
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return principal.isTraceEnabled(marker);
  }

  @Override
  public void trace(Marker marker, String msg) {
    principal.trace(marker, msg);
  }

  @Override
  public void trace(Marker marker, String format, Object arg) {
    principal.trace(marker, format, arg);
  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    principal.trace(marker, format, arg1, arg2);
  }

  @Override
  public void trace(Marker marker, String format, Object... argArray) {
    principal.trace(marker, format, argArray);
  }

  @Override
  public void trace(Marker marker, String msg, Throwable t) {
    principal.trace(marker, msg, t);
  }

  // ==================================== debug level ======================================

  @Override
  public boolean isDebugEnabled() {
    return principal.isDebugEnabled();
  }

  @Override
  public void debug(String msg) {
    principal.debug(msg);
  }

  /**
   * debug级别日志打印
   *
   * @param supplier 获取日志内容函数
   */
  public void debug(Supplier<String> supplier) {
    principal.debug(supplier.get());
  }

  @Override
  public void debug(String format, Object arg) {
    principal.debug(format, arg);
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    principal.debug(format, arg1, arg2);
  }

  @Override
  public void debug(String format, Object... arguments) {
    principal.debug(format, arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    principal.debug(msg, t);
  }

  /**
   * debug级别日志打印
   *
   * @param t 需要输入的异常日志内容
   */
  public void debug(Throwable t) {
    principal.debug("", t);
  }

  /**
   * debug级别日志打印
   *
   * @param t   需要输入的异常日志内容
   * @param msg 日志内容
   */
  public void debug(Throwable t, String msg) {
    principal.debug(msg, t);
  }

  /**
   * debug级别日志打印
   *
   * @param t        需要输入的异常日志内容
   * @param supplier 获取日志内容函数
   */
  public void debug(Throwable t, Supplier<String> supplier) {
    principal.debug(supplier.get(), t);
  }

  /**
   * debug级别日志打印
   *
   * @param t    需要输入的异常日志内容
   * @param msg  日志内容
   * @param args 日志参数
   */
  public void debug(Throwable t, String msg, Object... args) {
    principal.debug(MessageFormatter.arrayFormat(msg, args).getMessage(), t);
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return principal.isDebugEnabled(marker);
  }

  @Override
  public void debug(Marker marker, String msg) {
    principal.debug(marker, msg);
  }

  @Override
  public void debug(Marker marker, String format, Object arg) {
    principal.debug(marker, format, arg);
  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    principal.debug(marker, format, arg1, arg2);
  }

  @Override
  public void debug(Marker marker, String format, Object... argArray) {
    principal.debug(marker, format, argArray);
  }

  @Override
  public void debug(Marker marker, String msg, Throwable t) {
    principal.debug(marker, msg, t);
  }

  // ==================================== info level ======================================

  @Override
  public boolean isInfoEnabled() {
    return principal.isInfoEnabled();
  }

  @Override
  public void info(String msg) {
    principal.info(msg);
  }

  /**
   * info级别日志打印
   *
   * @param supplier 获取日志内容函数
   */
  public void info(Supplier<String> supplier) {
    principal.info(supplier.get());
  }

  @Override
  public void info(String format, Object arg) {
    principal.info(format, arg);
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    principal.info(format, arg1, arg2);
  }

  @Override
  public void info(String format, Object... arguments) {
    principal.info(format, arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    principal.info(msg, t);
  }

  /**
   * info级别日志打印
   *
   * @param t 需要输入的异常日志内容
   */
  public void info(Throwable t) {
    principal.info("", t);
  }

  /**
   * info级别日志打印
   *
   * @param t   需要输入的异常日志内容
   * @param msg 日志内容
   */
  public void info(Throwable t, String msg) {
    principal.info(msg, t);
  }

  /**
   * info级别日志打印
   *
   * @param t        需要输入的异常日志内容
   * @param supplier 获取日志内容函数
   */
  public void info(Throwable t, Supplier<String> supplier) {
    principal.info(supplier.get(), t);
  }

  /**
   * info级别日志打印
   *
   * @param t    需要输入的异常日志内容
   * @param msg  日志内容
   * @param args 日志参数
   */
  public void info(Throwable t, String msg, Object... args) {
    principal.info(MessageFormatter.arrayFormat(msg, args).getMessage(), t);
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return principal.isInfoEnabled(marker);
  }

  @Override
  public void info(Marker marker, String msg) {
    principal.info(marker, msg);
  }

  @Override
  public void info(Marker marker, String format, Object arg) {
    principal.info(marker, format, arg);
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    principal.info(marker, format, arg1, arg2);
  }

  @Override
  public void info(Marker marker, String format, Object... argArray) {
    principal.info(marker, format, argArray);
  }

  @Override
  public void info(Marker marker, String msg, Throwable t) {
    principal.info(marker, msg, t);
  }

  // ==================================== warn level ======================================

  @Override
  public boolean isWarnEnabled() {
    return principal.isWarnEnabled();
  }

  @Override
  public void warn(String msg) {
    principal.warn(msg);
  }

  /**
   * warn级别日志打印
   *
   * @param supplier 获取日志内容函数
   */
  public void warn(Supplier<String> supplier) {
    principal.warn(supplier.get());
  }

  @Override
  public void warn(String format, Object arg) {
    principal.warn(format, arg);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    principal.warn(format, arg1, arg2);
  }

  @Override
  public void warn(String format, Object... arguments) {
    principal.warn(format, arguments);
  }

  @Override
  public void warn(String msg, Throwable t) {
    principal.warn(msg, t);
  }

  /**
   * warn级别日志打印
   *
   * @param t 需要输入的异常日志内容
   */
  public void warn(Throwable t) {
    principal.warn("", t);
  }

  /**
   * warn级别日志打印
   *
   * @param t   需要输入的异常日志内容
   * @param msg 日志内容
   */
  public void warn(Throwable t, String msg) {
    principal.warn(msg, t);
  }

  /**
   * warn级别日志打印
   *
   * @param t        需要输入的异常日志内容
   * @param supplier 获取日志内容函数
   */
  public void warn(Throwable t, Supplier<String> supplier) {
    principal.warn(supplier.get(), t);
  }

  /**
   * warn级别日志打印
   *
   * @param t    需要输入的异常日志内容
   * @param msg  日志内容
   * @param args 日志参数
   */
  public void warn(Throwable t, String msg, Object... args) {
    principal.warn(MessageFormatter.arrayFormat(msg, args).getMessage(), t);
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return principal.isWarnEnabled(marker);
  }

  @Override
  public void warn(Marker marker, String msg) {
    principal.warn(marker, msg);
  }

  @Override
  public void warn(Marker marker, String format, Object arg) {
    principal.warn(marker, format, arg);
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    principal.warn(marker, format, arg1, arg2);
  }

  @Override
  public void warn(Marker marker, String format, Object... argArray) {
    principal.warn(marker, format, argArray);
  }

  @Override
  public void warn(Marker marker, String msg, Throwable t) {
    principal.warn(marker, msg, t);
  }

  // ==================================== error level ======================================

  @Override
  public boolean isErrorEnabled() {
    return principal.isErrorEnabled();
  }

  @Override
  public void error(String msg) {
    principal.error(msg);
  }

  /**
   * error级别日志打印
   *
   * @param supplier 获取日志内容函数
   */
  public void error(Supplier<String> supplier) {
    principal.error(supplier.get());
  }

  @Override
  public void error(String format, Object arg) {
    principal.error(format, arg);
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    principal.error(format, arg1, arg2);
  }

  @Override
  public void error(String format, Object... arguments) {
    principal.error(format, arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    principal.error(msg, t);
  }

  /**
   * error级别日志打印
   *
   * @param t 需要输入的异常日志内容
   */
  public void error(Throwable t) {
    principal.error("", t);
  }

  /**
   * error级别日志打印
   *
   * @param t   需要输入的异常日志内容
   * @param msg 日志内容
   */
  public void error(Throwable t, String msg) {
    principal.error(msg, t);
  }

  /**
   * error级别日志打印
   *
   * @param t        需要输入的异常日志内容
   * @param supplier 获取日志内容函数
   */
  public void error(Throwable t, Supplier<String> supplier) {
    principal.error(supplier.get(), t);
  }

  /**
   * error级别日志打印
   *
   * @param t    需要输入的异常日志内容
   * @param msg  日志内容
   * @param args 日志参数
   */
  public void error(Throwable t, String msg, Object... args) {
    principal.error(MessageFormatter.arrayFormat(msg, args).getMessage(), t);
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return principal.isErrorEnabled(marker);
  }

  @Override
  public void error(Marker marker, String msg) {
    principal.error(marker, msg);
  }

  @Override
  public void error(Marker marker, String format, Object arg) {
    principal.error(marker, format, arg);
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    principal.error(marker, format, arg1, arg2);
  }

  @Override
  public void error(Marker marker, String format, Object... argArray) {
    principal.error(marker, format, argArray);
  }

  @Override
  public void error(Marker marker, String msg, Throwable t) {
    principal.error(marker, msg, t);
  }

  // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ENHANCE FLUENT API >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  @Override
  public LabzenLoggingEventBuilder atError() {
    if (principal.isErrorEnabled()) {
      return new LabzenLoggingEventBuilder(this, Level.ERROR);
    } else {
      return LabzenNOPLoggingEventBuilder.singleton();
    }
  }

  @Override
  public LabzenLoggingEventBuilder atWarn() {
    if (principal.isWarnEnabled()) {
      return new LabzenLoggingEventBuilder(this, Level.WARN);
    } else {
      return LabzenNOPLoggingEventBuilder.singleton();
    }
  }

  @Override
  public LabzenLoggingEventBuilder atInfo() {
    if (principal.isInfoEnabled()) {
      return new LabzenLoggingEventBuilder(this, Level.INFO);
    } else {
      return LabzenNOPLoggingEventBuilder.singleton();
    }
  }

  @Override
  public LabzenLoggingEventBuilder atDebug() {
    if (principal.isDebugEnabled()) {
      return new LabzenLoggingEventBuilder(this, Level.DEBUG);
    } else {
      return LabzenNOPLoggingEventBuilder.singleton();
    }
  }

  @Override
  public LabzenLoggingEventBuilder atTrace() {
    if (principal.isTraceEnabled()) {
      return new LabzenLoggingEventBuilder(this, Level.TRACE);
    } else {
      return LabzenNOPLoggingEventBuilder.singleton();
    }
  }

  @Override
  public LabzenLoggingEventBuilder atLevel(Level level) {
    if (principal.isEnabledForLevel(level)) {
      return new LabzenLoggingEventBuilder(this, level);
    } else {
      return LabzenNOPLoggingEventBuilder.singleton();
    }
  }

  // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ENHANCE FLUENT API >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
}
