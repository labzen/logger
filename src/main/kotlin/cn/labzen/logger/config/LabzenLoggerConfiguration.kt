package cn.labzen.logger.config

internal data class LabzenLoggerConfiguration(
  val pattern: String = DEFAULT_PATTERN,
  val rootLevel: String = "INFO",
  val levels: Map<String, String> = emptyMap()
) {

  companion object {
    private const val DEFAULT_PATTERN =
      "%date{HH:mm:ss.SSS} %x_level [%thread] %-30x_logger{40} - %x_scene %x_tag %x_message%n"

    internal var instance = LabzenLoggerConfiguration()
  }
}
