package cn.labzen.logger.kernel;

import org.slf4j.Marker;
import org.slf4j.spi.LoggingEventBuilder;

import java.util.function.Supplier;

/**
 * 无操作（NOOP）日志事件构建器，用于日志级别被禁用时优化性能。
 *
 * <p>设计目的：
 * <ul>
 *   <li>避免创建真正的日志事件对象，减少内存开销</li>
 *   <li>提供无副作用的链式调用方法，所有操作均返回自身</li>
 *   <li>log()方法为空实现，不产生任何日志输出</li>
 * </ul>
 *
 * <p>使用场景：
 * <ul>
 *   <li>当日志级别被禁用时（如DEBUG级别日志在生产环境）</li>
 *   <li>Fluent API返回的Builder实例</li>
 * </ul>
 *
 * @see LabzenLogger#atDebug()
 * @see LabzenLogger#atInfo()
 */
public class LabzenNOPLoggingEventBuilder extends LabzenLoggingEventBuilder {

  /** 单例实例，避免重复创建对象 */
  static final LabzenNOPLoggingEventBuilder SINGLETON = new LabzenNOPLoggingEventBuilder();

  /**
   * 私有构造方法，确保只能通过单例获取实例
   *
   * @throws IllegalStateException 如果传入非null参数
   */
  private LabzenNOPLoggingEventBuilder() {
    super(null, null);
  }

  /**
   * 获取单例实例
   *
   * @return 共享的NOP构建器实例
   */
  public static LabzenNOPLoggingEventBuilder singleton() {
    return SINGLETON;
  }

  /**
   * 设置异常原因（无操作）
   *
   * @param cause 异常对象（被忽略）
   * @return 单例引用，支持链式调用
   */
  @Override
  public LoggingEventBuilder setCause(Throwable cause) {
    return singleton();
  }

  /**
   * 添加标记（无操作）
   *
   * @param marker 标记对象（被忽略）
   * @return 单例引用
   */
  @Override
  public LoggingEventBuilder addMarker(Marker marker) {
    return singleton();
  }

  /**
   * 添加参数（无操作）
   *
   * @param p 参数对象（被忽略）
   * @return 单例引用
   */
  @Override
  public LoggingEventBuilder addArgument(Object p) {
    return singleton();
  }

  /**
   * 通过Supplier添加参数（无操作）
   *
   * @param objectSupplier 参数提供者（被忽略）
   * @return 单例引用
   */
  @Override
  public LoggingEventBuilder addArgument(Supplier<?> objectSupplier) {
    return singleton();
  }

  /**
   * 添加键值对（无操作）
   *
   * @param key   键名（被忽略）
   * @param value 键值（被忽略）
   * @return 单例引用
   */
  @Override
  public LoggingEventBuilder addKeyValue(String key, Object value) {
    return singleton();
  }

  /**
   * 通过Supplier添加键值对（无操作）
   *
   * @param key           键名（被忽略）
   * @param valueSupplier 值提供者（被忽略）
   * @return 单例引用
   */
  @Override
  public LoggingEventBuilder addKeyValue(String key, Supplier<Object> valueSupplier) {
    return singleton();
  }

  /**
   * 设置消息（无操作）
   *
   * @param message 消息字符串（被忽略）
   * @return 单例引用
   */
  @Override
  public LoggingEventBuilder setMessage(String message) {
    return singleton();
  }

  /**
   * 通过Supplier设置消息（无操作）
   *
   * @param messageSupplier 消息提供者（被忽略）
   * @return 单例引用
   */
  @Override
  public LoggingEventBuilder setMessage(Supplier<String> messageSupplier) {
    return singleton();
  }

  /**
   * 输出日志（无操作）
   * <p>此方法为空实现，不产生任何输出
   */
  @Override
  public void log() {
  }

  /**
   * 输出日志（无操作）
   *
   * @param message 消息（被忽略）
   */
  @Override
  public void log(String message) {
  }

  /**
   * 输出日志（无操作）
   *
   * @param message 消息模板（被忽略）
   * @param arg     参数（被忽略）
   */
  @Override
  public void log(String message, Object arg) {
  }

  /**
   * 输出日志（无操作）
   *
   * @param message 消息模板（被忽略）
   * @param arg0    参数1（被忽略）
   * @param arg1    参数2（被忽略）
   */
  @Override
  public void log(String message, Object arg0, Object arg1) {
  }

  /**
   * 输出日志（无操作）
   *
   * @param message 消息模板（被忽略）
   * @param args    参数数组（被忽略）
   */
  @Override
  public void log(String message, Object... args) {
  }

  /**
   * 输出日志（无操作）
   *
   * @param messageSupplier 消息提供者（被忽略）
   */
  @Override
  public void log(Supplier<String> messageSupplier) {
  }
}