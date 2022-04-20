package cn.labzen.logger.config.loader

import cn.labzen.logger.config.ConfigurationLoader
import cn.labzen.logger.config.LabzenLoggerConfiguration

internal class SpringApplicationPropertiesConfigurationLoader : ConfigurationLoader {

  override fun load(): LabzenLoggerConfiguration? {
    return null
  }
}
