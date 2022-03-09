package cn.labzen.logger.logback.pattern.converter

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.CompositeConverter
import ch.qos.logback.core.pattern.color.ANSIConstants.*
import cn.labzen.logger.core.marker.MarkerWrapper

class SceneConverter : CompositeConverter<ILoggingEvent>() {

  override fun transform(event: ILoggingEvent, text: String?): String =
    parseMarker(event)

  private fun parseMarker(event: ILoggingEvent) =
    event.marker?.let { wrapper ->
      (wrapper as MarkerWrapper).scene?.let { scene ->
        "$ESC_START${scene.color}$ESC_END${scene.text}${ESC_START}0;$DEFAULT_FG$ESC_END"
      } ?: ""
    } ?: ""
}
