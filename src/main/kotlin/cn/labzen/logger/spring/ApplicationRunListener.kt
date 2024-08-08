package cn.labzen.logger.spring

import org.springframework.boot.ConfigurableBootstrapContext
import org.springframework.boot.SpringApplication
import org.springframework.boot.SpringApplicationRunListener
import org.springframework.core.env.ConfigurableEnvironment

class ApplicationRunListener(
  private val application: SpringApplication,
  private val args: Array<String>
) : SpringApplicationRunListener {

  override fun environmentPrepared(
    bootstrapContext: ConfigurableBootstrapContext,
    environment: ConfigurableEnvironment
  ) {
    if (environment.activeProfiles.isEmpty()) {
      Profiles.currentProfiles = listOf(Profiles.DEFAULT)
    } else {
      Profiles.currentProfiles = environment.activeProfiles.toList()
    }
  }
}
