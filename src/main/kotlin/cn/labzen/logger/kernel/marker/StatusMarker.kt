package cn.labzen.logger.kernel.marker

class StatusMarker(private val text: String) : AbstractMarker() {

  override fun toString(): String =
    text
}
