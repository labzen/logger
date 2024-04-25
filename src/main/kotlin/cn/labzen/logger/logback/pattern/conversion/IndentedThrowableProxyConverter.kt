package cn.labzen.logger.logback.pattern.conversion

import ch.qos.logback.classic.pattern.ThrowableProxyConverter
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.CoreConstants.LINE_SEPARATOR

class IndentedThrowableProxyConverter : ThrowableProxyConverter() {

  override fun throwableProxyToString(tp: IThrowableProxy?): String =
    StringBuilder(BUILDER_CAPACITY).apply {
      this.append(FRAME_LINE_START).append(LINE_SEPARATOR)
      recursiveAppend(this, null, 0, tp)
      this.append(FRAME_LINE_END).append(LINE_SEPARATOR)
    }.toString()

  private fun indent(buf: StringBuilder, space: Int) {
    buf.append(FRAME_PREFIX)
    if (space == 1) {
      buf.append(" ")
    } else if (space > 1) {
      for (i in 1..space) {
        buf.append(" ")
      }
    }
  }

  private fun recursiveAppend(sb: StringBuilder, prefix: String?, indent: Int, tp: IThrowableProxy?) {
    tp ?: return

    indent(sb, indent)
    prefix?.apply { sb.append(this) }
    sb.append(tp.className).append(": ").append(tp.message)
    sb.append(LINE_SEPARATOR)

    subjoinSTEPArray(sb, indent + 2, tp)

    val suppressed = tp.suppressed
    if (suppressed != null) {
      for (current in suppressed) {
        recursiveAppend(sb, CoreConstants.SUPPRESSED, indent + 2, current)
      }
    }
    recursiveAppend(sb, CoreConstants.CAUSED_BY, indent, tp.cause)
  }

  override fun subjoinSTEPArray(buf: StringBuilder, indent: Int, tp: IThrowableProxy) {
    val elements = tp.stackTraceElementProxyArray
    val maxIndex = elements.size - tp.commonFrames

    for (i in 0 until maxIndex) {
      val element = elements[i]

      indent(buf, indent)
      buf.append(element)
      buf.append(LINE_SEPARATOR)
    }

    if (tp.commonFrames > 0) {
      indent(buf, indent)
      buf.append("... ").append(tp.commonFrames).append(" common frames omitted").append(LINE_SEPARATOR)
    }
  }

  companion object {
    private const val FRAME_LINE_START =
      "  ┌──────────── ======\u001B[31m Exception Stack Information \u001B[39m====== ────────────────────────"
    private const val FRAME_PREFIX = "  │"
    private const val FRAME_LINE_END =
      "  └───────────────────────────────────────────────────────────────────────────────"
  }
}
