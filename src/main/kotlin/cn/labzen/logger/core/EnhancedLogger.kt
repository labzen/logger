package cn.labzen.logger.core

import org.slf4j.event.Level
import org.slf4j.helpers.MessageFormatter

class EnhancedLogger
internal constructor(private val principal: ch.qos.logback.classic.Logger) :
  org.slf4j.Logger by principal {

  // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> TRADITIONAL API >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

  // ==================================== trace level ======================================

  /**
   * trace级别日志打印
   * @param msgFunc 获取日志内容函数
   */
  fun trace(msgFunc: () -> String) {
    principal.trace(msgFunc())
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
   * @param msg 日志内容
   */
  fun trace(throwable: Throwable, msg: String) {
    principal.trace(msg, throwable)
  }

  /**
   * trace级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param msgFunc 获取日志内容函数
   */
  fun trace(throwable: Throwable, msgFunc: () -> String) {
    principal.trace(msgFunc(), throwable)
  }

  /**
   * trace级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param msgPattern 日志模板
   * @param args 日志参数
   */
  fun trace(throwable: Throwable, msgPattern: String, vararg args: Any?) {
    principal.trace(MessageFormatter.arrayFormat(msgPattern, args).message, throwable)
  }

  // ==================================== debug level ======================================

  /**
   * debug级别日志打印
   * @param msgFunc 获取日志内容函数
   */
  fun debug(msgFunc: () -> String) {
    principal.debug(msgFunc())
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
   * @param msg 日志内容
   */
  fun debug(throwable: Throwable, msg: String) {
    principal.debug(msg, throwable)
  }

  /**
   * debug级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param msgFunc 获取日志内容函数
   */
  fun debug(throwable: Throwable, msgFunc: () -> String) {
    principal.debug(msgFunc(), throwable)
  }

  /**
   * debug级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param msgPattern 日志模板
   * @param args 日志参数
   */
  fun debug(throwable: Throwable, msgPattern: String, vararg args: Any?) {
    principal.debug(MessageFormatter.arrayFormat(msgPattern, args).message, throwable)
  }

  // ==================================== info level ======================================

  /**
   * info级别日志打印
   * @param msgFunc 获取日志内容函数
   */
  fun info(msgFunc: () -> String) {
    principal.info(msgFunc())
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
   * @param msg 日志内容
   */
  fun info(throwable: Throwable, msg: String) {
    principal.info(msg, throwable)
  }

  /**
   * info级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param msgFunc 获取日志内容函数
   */
  fun info(throwable: Throwable, msgFunc: () -> String) {
    principal.info(msgFunc(), throwable)
  }

  /**
   * info级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param msgPattern 日志模板
   * @param args 日志参数
   */
  fun info(throwable: Throwable, msgPattern: String, vararg args: Any?) {
    principal.info(MessageFormatter.arrayFormat(msgPattern, args).message, throwable)
  }

  // ==================================== warn level ======================================

  /**
   * warn级别日志打印
   * @param msgFunc 获取日志内容函数
   */
  fun warn(msgFunc: () -> String) {
    principal.warn(msgFunc())
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
   * @param msg 日志内容
   */
  fun warn(throwable: Throwable, msg: String) {
    principal.warn(msg, throwable)
  }

  /**
   * warn级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param msgFunc 获取日志内容函数
   */
  fun warn(throwable: Throwable, msgFunc: () -> String) {
    principal.warn(msgFunc(), throwable)
  }

  /**
   * warn级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param msgPattern 日志模板
   * @param args 日志参数
   */
  fun warn(throwable: Throwable, msgPattern: String, vararg args: Any?) {
    principal.warn(MessageFormatter.arrayFormat(msgPattern, args).message, throwable)
  }

  // ==================================== error level ======================================

  /**
   * error级别日志打印
   * @param msgFunc 获取日志内容函数
   */
  fun error(msgFunc: () -> String) {
    principal.error(msgFunc())
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
   * @param msg 日志内容
   */
  fun error(throwable: Throwable, msg: String) {
    principal.error(msg, throwable)
  }

  /**
   * error级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param msgFunc 获取日志内容函数
   */
  fun error(throwable: Throwable, msgFunc: () -> String) {
    principal.error(msgFunc(), throwable)
  }

  /**
   * error级别日志打印
   * @param throwable 需要输入的异常日志内容
   * @param msgPattern 日志模板
   * @param args 日志参数
   */
  fun error(throwable: Throwable, msgPattern: String, vararg args: Any?) {
    principal.error(MessageFormatter.arrayFormat(msgPattern, args).message, throwable)
  }

  // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> FLUENT API >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
  // pipe functions

  fun trace() = PipedLogger(principal, Level.TRACE)
  fun debug() = PipedLogger(principal, Level.DEBUG)
  fun info() = PipedLogger(principal, Level.INFO)
  fun warn() = PipedLogger(principal, Level.WARN)
  fun error() = PipedLogger(principal, Level.ERROR)
}
