package cn.labzen.logger.kernel

import org.slf4j.Logger
import org.slf4j.event.Level
import org.slf4j.helpers.MessageFormatter
import java.util.function.Supplier

class LabzenLogger(private val principal: Logger) : Logger by principal {

  // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ENHANCE TRADITIONAL API >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // ==================================== trace level ======================================

  /**
   * trace级别日志打印
   * @param messageSupplier 获取日志内容函数
   */
  fun trace(messageSupplier: Supplier<String>) {
    principal.trace(messageSupplier.get())
  }

  /**
   * trace级别日志打印
   * @param throwable 需要输入的异常日志内容
   */
  fun trace(throwable: Throwable) {
    principal.trace("", throwable)
  }

  /**
   * trace级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param message 日志内容
   */
  fun trace(throwable: Throwable, message: String) {
    principal.trace(message, throwable)
  }

  /**
   * trace级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param messageSupplier 获取日志内容函数
   */
  fun trace(throwable: Throwable, messageSupplier: Supplier<String>) {
    principal.trace(messageSupplier.get(), throwable)
  }

  /**
   * trace级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param messagePattern 日志模板
   * @param args 日志参数
   */
  fun trace(throwable: Throwable, messagePattern: String, vararg args: Any?) {
    principal.trace(MessageFormatter.arrayFormat(messagePattern, args).message, throwable)
  }

  // ==================================== debug level ======================================

  /**
   * debug级别日志打印
   * @param messageSupplier 获取日志内容函数
   */
  fun debug(messageSupplier: Supplier<String>) {
    principal.debug(messageSupplier.get())
  }

  /**
   * debug级别日志打印
   * @param throwable 需要输入的异常日志内容
   */
  fun debug(throwable: Throwable) {
    principal.debug("", throwable)
  }

  /**
   * debug级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param message 日志内容
   */
  fun debug(throwable: Throwable, message: String) {
    principal.debug(message, throwable)
  }

  /**
   * debug级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param messageSupplier 获取日志内容函数
   */
  fun debug(throwable: Throwable, messageSupplier: Supplier<String>) {
    principal.debug(messageSupplier.get(), throwable)
  }

  /**
   * debug级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param messagePattern 日志模板
   * @param args 日志参数
   */
  fun debug(throwable: Throwable, messagePattern: String, vararg args: Any?) {
    principal.debug(MessageFormatter.arrayFormat(messagePattern, args).message, throwable)
  }

  // ==================================== info level ======================================

  /**
   * info级别日志打印
   * @param messageSupplier 获取日志内容函数
   */
  fun info(messageSupplier: Supplier<String>) {
    principal.info(messageSupplier.get())
  }

  /**
   * info级别日志打印
   * @param throwable 需要输入的异常日志内容
   */
  fun info(throwable: Throwable) {
    principal.info("", throwable)
  }

  /**
   * info级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param message 日志内容
   */
  fun info(throwable: Throwable, message: String) {
    principal.info(message, throwable)
  }

  /**
   * info级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param messageSupplier 获取日志内容函数
   */
  fun info(throwable: Throwable, messageSupplier: Supplier<String>) {
    principal.info(messageSupplier.get(), throwable)
  }

  /**
   * info级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param messagePattern 日志模板
   * @param args 日志参数
   */
  fun info(throwable: Throwable, messagePattern: String, vararg args: Any?) {
    principal.info(MessageFormatter.arrayFormat(messagePattern, args).message, throwable)
  }

  // ==================================== warn level ======================================

  /**
   * warn级别日志打印
   * @param messageSupplier 获取日志内容函数
   */
  fun warn(messageSupplier: Supplier<String>) {
    principal.warn(messageSupplier.get())
  }

  /**
   * warn级别日志打印
   * @param throwable 需要输入的异常日志内容
   */
  fun warn(throwable: Throwable) {
    principal.warn("", throwable)
  }

  /**
   * warn级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param message 日志内容
   */
  fun warn(throwable: Throwable, message: String) {
    principal.warn(message, throwable)
  }

  /**
   * warn级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param messageSupplier 获取日志内容函数
   */
  fun warn(throwable: Throwable, messageSupplier: Supplier<String>) {
    principal.warn(messageSupplier.get(), throwable)
  }

  /**
   * warn级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param messagePattern 日志模板
   * @param args 日志参数
   */
  fun warn(throwable: Throwable, messagePattern: String, vararg args: Any?) {
    principal.warn(MessageFormatter.arrayFormat(messagePattern, args).message, throwable)
  }

  // ==================================== error level ======================================

  /**
   * error级别日志打印
   * @param messageSupplier 获取日志内容函数
   */
  fun error(messageSupplier: Supplier<String>) {
    principal.error(messageSupplier.get())
  }

  /**
   * error级别日志打印
   * @param throwable 需要输入的异常日志内容
   */
  fun error(throwable: Throwable) {
    principal.error("", throwable)
  }

  /**
   * error级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param message 日志内容
   */
  fun error(throwable: Throwable, message: String) {
    principal.error(message, throwable)
  }

  /**
   * error级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param messageSupplier 获取日志内容函数
   */
  fun error(throwable: Throwable, messageSupplier: Supplier<String>) {
    principal.error(messageSupplier.get(), throwable)
  }

  /**
   * error级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param messagePattern 日志模板
   * @param args 日志参数
   */
  fun error(throwable: Throwable, messagePattern: String, vararg args: Any?) {
    principal.error(MessageFormatter.arrayFormat(messagePattern, args).message, throwable)
  }

  // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ENHANCE FLUENT API >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  /**
   * 提供标准SLF4j Fluent API的扩展，实现更多的功能，不影响SLF4J官方原功能使用
   */
  fun trace() = LabzenLoggingEventBuilder(this, Level.TRACE)

  /**
   * 提供标准SLF4j Fluent API的扩展，实现更多的功能，不影响SLF4J官方原功能使用
   */
  fun debug() = LabzenLoggingEventBuilder(this, Level.DEBUG)

  /**
   * 提供标准SLF4j Fluent API的扩展，实现更多的功能，不影响SLF4J官方原功能使用
   */
  fun info() = LabzenLoggingEventBuilder(this, Level.INFO)

  /**
   * 提供标准SLF4j Fluent API的扩展，实现更多的功能，不影响SLF4J官方原功能使用
   */
  fun warn() = LabzenLoggingEventBuilder(this, Level.WARN)

  /**
   * 提供标准SLF4j Fluent API的扩展，实现更多的功能，不影响SLF4J官方原功能使用
   */
  fun error() = LabzenLoggingEventBuilder(this, Level.ERROR)

}
