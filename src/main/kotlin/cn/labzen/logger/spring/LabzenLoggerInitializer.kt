package cn.labzen.logger.spring

import cn.labzen.logger.logback.pattern.converter.IdentifiableLoggerConverter
import cn.labzen.logger.meta.LabzenMetaPrinter
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.Ordered

class LabzenLoggerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  override fun getOrder(): Int =
    Int.MIN_VALUE + 20

  override fun initialize(applicationContext: ConfigurableApplicationContext) {
    IdentifiableLoggerConverter.collectLabzenComponentPackages()
    LabzenMetaPrinter.print()
  }
}
