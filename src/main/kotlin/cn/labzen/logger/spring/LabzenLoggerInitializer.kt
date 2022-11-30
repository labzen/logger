package cn.labzen.logger.spring

import cn.labzen.logger.meta.LabzenMetaPrinter
import cn.labzen.logger.meta.LoggerConfiguration
import cn.labzen.meta.Labzens
import cn.labzen.meta.spring.SpringApplicationContextInitializerOrder
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.Ordered

class LabzenLoggerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  override fun getOrder(): Int =
    SpringApplicationContextInitializerOrder.MODULE_LOGGER_INITIALIZER_ORDER

  override fun initialize(applicationContext: ConfigurableApplicationContext) {
    val configuration = Labzens.configurationWith(LoggerConfiguration::class.java)

    if (configuration.printMetaInformation()) {
      LabzenMetaPrinter.print()
    }
  }
}
