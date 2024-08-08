package cn.labzen.logger.spring

object Profiles {

  const val DEFAULT: String = ""
  const val DEV: String = "dev"
  const val DEVELOPMENT: String = "development"
  const val TEST: String = "test"
  const val PROD: String = "prod"
  const val PRODUCTION: String = "production"

  internal lateinit var currentProfiles: List<String>

  // fun setCurrentProfiles(profiles: Array<String>) {
  //   currentProfiles = profiles.toList()
  // }
}
