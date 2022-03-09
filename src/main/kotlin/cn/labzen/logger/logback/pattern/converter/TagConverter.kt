package cn.labzen.logger.logback.pattern.converter

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.CompositeConverter
import ch.qos.logback.core.pattern.color.ANSIConstants.*
import cn.labzen.logger.core.marker.MarkerWrapper

class TagConverter : CompositeConverter<ILoggingEvent>() {

  override fun transform(event: ILoggingEvent, text: String?): String =
    parseMarker(event)

  private fun parseMarker(event: ILoggingEvent) =
    event.marker?.let { wrapper ->
      (wrapper as MarkerWrapper).tag?.let { tag ->
        tag.texts.joinToString(" ") {
          "${ESC_START}7;$WHITE_FG$ESC_END#$it${ESC_START}0;$DEFAULT_FG$ESC_END"
        }
      } ?: ""
    } ?: ""
}
