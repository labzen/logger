package cn.labzen.logger.meta

import cn.labzen.meta.configuration.annotation.Configured
import cn.labzen.meta.configuration.annotation.Item

@Configured("logger")
interface LoggerConfiguration {

  @Item(path = "print.meta", required = false, defaultValue = "false")
  fun printMetaInformation(): Boolean
}
