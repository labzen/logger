package cn.labzen.logger.kernel

import ch.qos.logback.core.CoreConstants.LINE_SEPARATOR
import cn.labzen.logger.kernel.enums.CodeTypes
import cn.labzen.logger.kernel.tile.MessagePatternTileManager
import org.slf4j.Logger
import org.slf4j.event.DefaultLoggingEvent
import org.slf4j.event.Level

class LabzenLoggingEvent(level: Level, logger: Logger) : DefaultLoggingEvent(level, logger) {

  internal var codeType: CodeTypes? = null
  internal var codeText: String? = null

  override fun getMessage(): String {
    val message = MessagePatternTileManager.transform(super.getMessage() ?: "", super.getArguments())

    return codeType?.let {
      """$message
  $FRAME_LINE_START_PREFIX${it.text}$FRAME_LINE_START_SUFFIX
  $FRAME_PREFIX${formattedCodeMessage(codeText!!)}
  $FRAME_LINE_END
      """.trimIndent()
    } ?: message
  }

  private fun formattedCodeMessage(msg: String) =
    msg.replace(LAST_CRLF_REGEX, "").replace(LINE_SEPARATOR, FRAME_PREFIX_WITH_CRLF)

  companion object {
    private const val FRAME_LINE_START_PREFIX = "┌──────────── ====== Lang: "
    private const val FRAME_LINE_START_SUFFIX =
      " ====== ─────────────────────────────────────────────────────────────────"
    private const val FRAME_PREFIX = "│ "
    private const val FRAME_LINE_END =
      "└───────────────────────────────────────────────────────────────────────────────────────────────────────"

    private val FRAME_PREFIX_WITH_CRLF = "$LINE_SEPARATOR  │ "
    private val LAST_CRLF_REGEX = Regex("$LINE_SEPARATOR$")
  }
}
