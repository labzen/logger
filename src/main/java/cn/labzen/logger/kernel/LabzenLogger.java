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

/**
 * Labzen日志增强器，包装底层Logger实现（如Logback/Reload4j）并提供增强功能。
 *
 * <p>本类实现了以下功能：
 * <ul>
 *   <li>消息前缀管理 - 支持在日志输出前后添加前缀标记</li>
 *   <li>Marker合并 - 将多个Marker和KeyValuePair合并到消息中</li>
 *   <li>Fluent API - 支持链式调用风格的日志构建</li>
 *   <li>Supplier支持 - 支持延迟计算日志内容的函数式接口</li>
 * </ul>
 */
public class LabzenLogger implements Logger, LoggingEventAware {

  /**
   * 底层的Logger实现，所有日志操作最终委托给它
   */
  private final Logger principal;

  /**
   * 消息前缀字符串，用于在每条日志消息前添加统一前缀
   */
  private String messagePrefix;

  /**
   * 前缀是否生效标志，配合changeMessagePrefixAfter实现延迟生效
   */
  private boolean messagePrefixEnabled = false;

  /**
   * 延迟切换前缀状态标志，用于实现"从下一条日志开始生效"的语义
   * <p>true表示下一次log()调用后需要切换messagePrefixEnabled状态
   */
  private boolean changeMessagePrefixAfter = false;

  /**
   * 构造方法，传入底层Logger实现
   *
   * @param principal 底层Logger实现，不能为null
   */
  public LabzenLogger(Logger principal) {
    this.principal = principal;
  }

  /**
   * 开始消息前缀，支持立即生效或延迟到下一条日志生效
   *
   * <p>立即生效模式(immediately=true)：
   * <ul>
   *   <li>当前日志开始即添加前缀</li>
   *   <li>changeMessagePrefixAfter设为false</li>
   * </ul>
   *
   * <p>延迟生效模式(immediately=false)：
   * <ul>
   *   <li>下一条日志开始才添加前缀</li>
   *   <li>changeMessagePrefixAfter设为true，在log()中切换状态</li>
   * </ul>
   *
   * @param prefix      前缀字符串
   * @param immediately true-立即生效，false-下一条日志开始生效
   */
  public void startMessagePrefix(String prefix, boolean immediately) {
    messagePrefix = prefix;
    messagePrefixEnabled = immediately;
    changeMessagePrefixAfter = !immediately;
  }

  /**
   * 结束消息前缀，支持立即生效或延迟到下一条日志生效
   *
   * <p>语义与{@link #startMessagePrefix(String, boolean)}相反
   *
   * @param immediately true-立即停止，false-下一条日志开始停止
   */
  public void endMessagePrefix(boolean immediately) {
    messagePrefix = null;
    messagePrefixEnabled = !immediately;
    changeMessagePrefixAfter = !immediately;
  }

  /**
   * 获取当前配置的消息前缀
   *
   * @return 前缀字符串，可能为null
   */
  public String getMessagePrefix() {
    return messagePrefix;
  }

  /**
   * 核心日志处理方法，实现{@link LoggingEventAware}接口
   *
   * <p>处理流程：
   * <ol>
   *   <li>获取原始消息，并通过MessageFormatter处理占位符</li>
   *   <li>合并Markers到消息前缀中</li>
   *   <li>追加KeyValuePairs到消息末尾</li>
   *   <li>处理延迟的前缀状态切换</li>
   *   <li>根据日志级别调用对应的principal方法</li>
   * </ol>
   *
   * <p>注意：此方法已同步，防止并发修改messagePrefixEnabled状态
   *
   * @param event SLF4J的LoggingEvent事件对象
   */
  public synchronized void log(LoggingEvent event) {
    // 1. 获取并预处理消息：合并Markers、处理占位符、追加KeyValuePairs
    String preprocessedMessage = mergeMarkersAndKeyValuePairs(event, event.getMessage());

    // 2. 处理延迟前缀切换：如果设置了changeMessagePrefixAfter，则在本次log后切换状态
    if (changeMessagePrefixAfter) {
      changeMessagePrefixAfter = false;
      messagePrefixEnabled = !messagePrefixEnabled;
    }

    // 3. 检查是否有异常需要一起记录
    boolean hasException = event.getThrowable() != null;

    // 4. 根据日志级别分发到对应的principal方法
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
      // 当level为null时，默认降级为INFO级别
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
   * 合并Markers和KeyValuePairs到消息中
   *
   * <p>消息格式：[markers...] [formatted_message] [key=value]...
   *
   * <p>实现细节：
   * <ul>
   *   <li>Markers通过调用其toString()追加到消息开头</li>
   *   <li>消息占位符通过MessageFormatter.arrayFormat处理</li>
   *   <li>KeyValuePairs以key=value格式追加到消息末尾</li>
   * </ul>
   *
   * @param event   日志事件对象，包含markers和key-value pairs
   * @param message 原始消息模板
   * @return 预处理后的完整消息字符串
   */
  private String mergeMarkersAndKeyValuePairs(LoggingEvent event, String message) {
    StringBuilder sb = new StringBuilder();

    // 1. 追加所有Markers
    if (event.getMarkers() != null) {
      for (Marker marker : event.getMarkers()) {
        sb.append(marker).append(" ");
      }
    }

    // 2. 追加格式化后的消息内容（处理{}占位符）
    sb.append(MessageFormatter.arrayFormat(message, event.getArgumentArray()).getMessage());

    // 3. 追加所有KeyValuePairs
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
