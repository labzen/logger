package cn.labzen.logger.kernel.marker

import ch.qos.logback.core.pattern.color.ANSIConstants.*

class StatusMarker(private val text: String, private val color: String) : AbstractMarker() {

  override fun toString(): String =
    "$ESC_START$color$ESC_END$text${ESC_START}0;$DEFAULT_FG$ESC_END"
}
