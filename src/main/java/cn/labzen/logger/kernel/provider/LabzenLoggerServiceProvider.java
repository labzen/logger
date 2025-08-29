package cn.labzen.logger.kernel.provider;

import cn.labzen.logger.LoggerImplementor;
import cn.labzen.logger.logback.LabzenLogbackServiceProvider;
import cn.labzen.logger.reload4j.LabzenReload4jServiceProvider;
import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;
//import sun.misc.Unsafe;

public class LabzenLoggerServiceProvider implements SLF4JServiceProvider {

  private final SLF4JServiceProvider providerDelegator;

  public LabzenLoggerServiceProvider() {
    LoggerImplementor.detect();

    if (LoggerImplementor.isLogbackPresent()) {
      providerDelegator = new LabzenLogbackServiceProvider();
    } else if (LoggerImplementor.isReload4jPresent()) {
      providerDelegator = new LabzenReload4jServiceProvider();
    } else {
      throw new RuntimeException("没有Logback或Reload4j的依赖");
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

  @Override
  public ILoggerFactory getLoggerFactory() {
    return providerDelegator.getLoggerFactory();
  }

  @Override
  public IMarkerFactory getMarkerFactory() {
    return providerDelegator.getMarkerFactory();
  }

  @Override
  public MDCAdapter getMDCAdapter() {
    return providerDelegator.getMDCAdapter();
  }

  @Override
  public String getRequestedApiVersion() {
    return providerDelegator.getRequestedApiVersion();
  }

  @Override
  public void initialize() {
    providerDelegator.initialize();
  }
}
