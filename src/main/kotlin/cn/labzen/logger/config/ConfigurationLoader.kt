package cn.labzen.logger.config

import cn.labzen.logger.config.loader.LabzenPropertiesConfigurationLoader
import cn.labzen.logger.config.loader.LabzenYAMLConfigurationLoader
import cn.labzen.logger.config.loader.SpringApplicationPropertiesConfigurationLoader
import cn.labzen.logger.config.loader.SpringApplicationYAMLConfigurationLoader

internal interface ConfigurationLoader {

  fun load(): LabzenLoggerConfiguration?

  companion object {

    private val loaders = listOf(
      LabzenYAMLConfigurationLoader(),
      LabzenPropertiesConfigurationLoader(),
      SpringApplicationYAMLConfigurationLoader(),
      SpringApplicationPropertiesConfigurationLoader()
    )

    fun loadOneByOne() {
      val instance = loaders.firstNotNullOfOrNull { it.load() }
      instance?.also {
        LabzenLoggerConfiguration.instance = it
      }
    }
  }
}
