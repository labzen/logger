package cn.labzen.logger.spring

import cn.labzen.logger.meta.LabzenMetaPrinter
import cn.labzen.logger.meta.LoggerConfiguration
import cn.labzen.meta.Labzens
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.Ordered

class LabzenLoggerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  override fun getOrder(): Int =
    Int.MIN_VALUE + 20

  override fun initialize(applicationContext: ConfigurableApplicationContext) {
    val configuration = Labzens.configurationWith(LoggerConfiguration::class.java)

    if (configuration.printMetaInformation()) {
      LabzenMetaPrinter.print()
    }
  }
}
