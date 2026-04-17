package cn.labzen.logger.kernel.provider;

import cn.labzen.logger.LoggerImplementor;
import cn.labzen.logger.logback.LabzenLogbackServiceProvider;
import cn.labzen.logger.reload4j.LabzenReload4jServiceProvider;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * Labzen日志服务提供者，实现SLF4J的ServiceProvider接口。
 *
 * <p>职责：
 * <ul>
 *   <li>探测项目中使用的日志框架（Logback或Reload4j）</li>
 *   <li>委托给对应框架的ServiceProvider</li>
 *   <li>提供统一的Logger工厂</li>
 * </ul>
 *
 * <p>使用方式：
 * <ul>
 *   <li>通过{@link cn.labzen.logger.Loggers#enhance()}设置系统属性</li>
 *   <li>SLF4J 2.0.9+通过slf4j.provider属性直接指定</li>
 *   <li>或通过META-INF/services自动发现</li>
 * </ul>
 *
 * @see SLF4JServiceProvider
 * @see LabzenLogbackServiceProvider
 * @see cn.labzen.logger.logback.LabzenLogbackLoggerContext
 */
public class LabzenLoggerServiceProvider implements SLF4JServiceProvider {

  /** 委托的底层ServiceProvider */
  private final SLF4JServiceProvider providerDelegator;

  /**
   * 构造方法，探测并初始化底层Provider
   */
  public LabzenLoggerServiceProvider() {
    // 1. 探测可用的日志框架
    LoggerImplementor.detect();

    // 2. 根据探测结果创建对应的Provider
    if (LoggerImplementor.isLogbackPresent()) {
      providerDelegator = new LabzenLogbackServiceProvider();
    } else if (LoggerImplementor.isReload4jPresent()) {
      providerDelegator = new LabzenReload4jServiceProvider();
    } else {
      throw new IllegalStateException("没有Logback或Reload4j的依赖");
    }

    //disableIllegalReflectiveWarning();
  }

  /**
   * 忽略非法反射警告 适用于jdk11
   * <p>
   * 隐藏因为 javassist 包做字节码操作产生的警告信息；可能放在这不是很合适，以后移出去
   * <p>
   * 实际输出的警告为：
   * WARNING: An illegal reflective access operation has occurred<br/>
   * WARNING: Illegal reflective access by javassist.util.proxy.SecurityActions (file:/Users/dean/.m2/repository/org/javassist/javassist/3.28.0-GA/javassist-3.28.0-GA.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)<br/>
   * WARNING: Please consider reporting this to the maintainers of javassist.util.proxy.SecurityActions<br/>
   * WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations<br/>
   * WARNING: All illegal access operations will be denied in a future release
   */
  //private void disableIllegalReflectiveWarning() {
  //  try {
  //    Class<Unsafe> unsafeClass = Unsafe.class;
  //    unsafeClass.getDeclaredField("theUnsafe");
  //  } catch (Exception e) {
  //    // ignore that
  //  }
  //}

  /**
   * 获取Logger工厂
   *
   * @return Labzen封装的Logger工厂
   */
  @Override
  public ILoggerFactory getLoggerFactory() {
    return providerDelegator.getLoggerFactory();
  }

  /**
   * 获取Marker工厂
   *
   * @return 委托Provider的Marker工厂
   */
  @Override
  public IMarkerFactory getMarkerFactory() {
    return providerDelegator.getMarkerFactory();
  }

  /**
   * 获取MDC适配器
   *
   * @return 委托Provider的MDC适配器
   */
  @Override
  public MDCAdapter getMDCAdapter() {
    return providerDelegator.getMDCAdapter();
  }

  /**
   * 获取请求的API版本
   *
   * @return SLF4J API版本
   */
  @Override
  public String getRequestedApiVersion() {
    return providerDelegator.getRequestedApiVersion();
  }

  /**
   * 初始化Provider
   *
   * <p>调用底层Provider的initialize方法
   */
  @Override
  public void initialize() {
    providerDelegator.initialize();
  }
}