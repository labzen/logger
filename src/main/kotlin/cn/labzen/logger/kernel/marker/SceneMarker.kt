package cn.labzen.logger.kernel.marker

import ch.qos.logback.core.pattern.color.ANSIConstants.*

data class SceneMarker(private val text: String) : AbstractMarker() {

  override fun toString(): String =
    "<$text>"
}
