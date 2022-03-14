package cn.labzen.logger.logback.pattern.converter

import ch.qos.logback.classic.pattern.ClassicConverter
import ch.qos.logback.classic.spi.ILoggingEvent
import cn.labzen.logger.core.marker.CodeMarker
import cn.labzen.logger.core.marker.MarkerWrapper
import cn.labzen.logger.core.pipe.MessagePatternManager

class PipelineMessageConverter : ClassicConverter() {

  override fun convert(event: ILoggingEvent): String =
    event.marker?.let {
      (it as MarkerWrapper).code?.let { cm ->
        codeMessage(event, cm)
      } ?: MessagePatternManager.transform(event.message, event.argumentArray)
    } ?: event.formattedMessage

  private fun codeMessage(event: ILoggingEvent, cm: CodeMarker): String =
    formattedCodeMessage(cm.text).let {
      """${event.message ?: ""}
  $FRAME_LINE_START_PREFIX${cm.type}$FRAME_LINE_START_SUFFIX
  $FRAME_PREFIX$it
  $FRAME_LINE_END
      """
    }

  private fun formattedCodeMessage(msg: String) =
    msg.replace(LAST_CRLF_REGEX, "").replace(CRLF, FRAME_PREFIX_WITH_CRLF)

  companion object {
    private const val FRAME_LINE_START_PREFIX = "┌─────────===\u001B[7;36m Lang: "
    private const val FRAME_LINE_START_SUFFIX =
      " \u001B[0;39m===──────────────────────────────────────────────────────"
    private const val FRAME_PREFIX = "│ "
    private const val FRAME_PREFIX_WITH_CRLF = "\r\n  │ "
    private const val FRAME_LINE_END =
      "└───────────────────────────────────────────────────────────────────────────────"
    private const val CRLF = "\r\n"

    private val LAST_CRLF_REGEX = Regex("\r\n$")
  }
}
