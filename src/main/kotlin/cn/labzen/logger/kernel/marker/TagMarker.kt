package cn.labzen.logger.kernel.marker

import ch.qos.logback.core.pattern.color.ANSIConstants.*

class TagMarker(private val texts: List<String>) : AbstractMarker() {

  override fun toString(): String =
    texts.joinToString(" ") {
      "[${ESC_START}4;$BLUE_FG$ESC_END$it${ESC_START}0;$DEFAULT_FG$ESC_END]"
    }
}
