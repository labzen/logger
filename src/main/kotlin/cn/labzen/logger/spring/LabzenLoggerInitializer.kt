package cn.labzen.logger.spring

import cn.labzen.logger.logback.pattern.conversion.IdentifiableLoggerConverter
import cn.labzen.logger.meta.LabzenMetaPrinter
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.Ordered

class LabzenLoggerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

  override fun getOrder(): Int =
    Int.MIN_VALUE + 20

  override fun initialize(applicationContext: ConfigurableApplicationContext) {
    // todo 靠配置文件，看打印与否
    IdentifiableLoggerConverter.collectLabzenComponentPackages()
    LabzenMetaPrinter.print()
  }
}
