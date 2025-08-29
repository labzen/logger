package cn.labzen.logger.spring;

import cn.labzen.logger.meta.LoggerConfiguration;
import cn.labzen.logger.print.MetaPrinter;
import cn.labzen.meta.Labzens;
import cn.labzen.meta.spring.SpringInitializationOrder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import javax.annotation.Nonnull;

/**
 * 在Spring Boot的项目启动时执行
 */
public class LabzenLoggerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  @Override
  public int getOrder() {
    return SpringInitializationOrder.MODULE_LOGGER_INITIALIZER_ORDER;
  }

  @Override
  public void initialize(@Nonnull ConfigurableApplicationContext applicationContext) {
    LoggerConfiguration configuration = Labzens.configurationWith(LoggerConfiguration.class);

    // 打印 Labzen 模组信息
    if (configuration != null && configuration.printMetaInformation()) {
      MetaPrinter.print();
    }
  }
}
