package cn.labzen.logger;

import cn.labzen.logger.kernel.LabzenLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * 获取 Logger 实例，工厂类（等同于 Slf4j 的 {@link LoggerFactory}），用于替代 Slf4j 的工厂类，直接获取增强后的 {@link Logger}；
 * <p>
 * 不使用本工厂类，而使用 Lombok 的 @Slf4j 注解，或使用 {@link LoggerFactory} 获取的 Slf4j {@link Logger}接口，其具体实现为 {@link LabzenLogger}
 */
public final class Loggers {

  private Loggers() {
  }

  /**
   * <b>调用本方法，会确保 Labzen Logger 模块的 {@link SLF4JServiceProvider} 生效；如果不调用，则无法完全保证。</b>
   * <p>
   * 要通过 Labzen Logger 组件增强 SLF4j，则必须在第一次使用 {@link LoggerFactory#getLogger(String)} 调用之前
   * （包括使用 Lombok 的 @Slf4j 等注解获取{@link Logger}对象的方式）执行
   * <p>
   * 如果是Spring Boot项目，一般会在main函数中第一行调用，例如：
   * <code>
   * <pre>
   * &#64;SpringBootApplication
   * public class Launcher {
   *
   *   public static void main(String[] args) {
   *     // 在第一时间调用
   *     Loggers.enhance();
   *     SpringApplication.run(Launcher.class);
   *   }
   * }
   *   </pre>
   * </code>
   */
  public static void enhance() {
    // 针对 SLF4J 的 2.0.9 以后的版本，提供了 slf4j.provider 系统属性：显式指定provider类，这绕过了查找提供者的服务加载器机制，并可缩短SLF4J的初始化时间。
    // 提供定制的 provider 来增强 Logback 或 Reload4j 的功能
    System.setProperty(
        LoggerFactory.PROVIDER_PROPERTY_KEY,
        "cn.labzen.logger.kernel.provider.LabzenLoggerServiceProvider"
    );
  }

  public static LabzenLogger getLogger(String name) {
    return (LabzenLogger) LoggerFactory.getLogger(name);
  }

  public static LabzenLogger getLogger(Class<?> clazz) {
    return (LabzenLogger) LoggerFactory.getLogger(clazz);
  }
}
