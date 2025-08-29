package cn.labzen.logger.logback.pattern.conversion;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;

import static ch.qos.logback.core.CoreConstants.*;

public class IndentedThrowableProxyConverter extends ThrowableProxyConverter {

  private static final String FRAME_LINE_START = "  ┌──────────── ======\u001B[31m Exception Stack Information \u001B[39m====== ────────────────────────";
  private static final String FRAME_PREFIX = "  │";
  private static final String FRAME_LINE_END = "  " +
                                               "└───────────────────────────────────────────────────────────────────────────────";

  @Override
  protected String throwableProxyToString(IThrowableProxy tp) {
    StringBuilder buf = new StringBuilder(BUILDER_CAPACITY);
    buf.append(LINE_SEPARATOR).append(FRAME_LINE_START).append(LINE_SEPARATOR);
    recursiveAppend(buf, null, 0, tp);
    buf.append(FRAME_LINE_END).append(LINE_SEPARATOR);
    return buf.toString();
  }

  private void indent(StringBuilder builder, int space) {
    builder.append(FRAME_PREFIX);
    if (space >= 1) {
      builder.append(" ".repeat(space));
    }
  }

  private void recursiveAppend(StringBuilder buf, String prefix, int indent, IThrowableProxy tp) {
    if (tp == null) {
      return;
    }

    indent(buf, indent);
    if (prefix != null) {
      buf.append(prefix);
    }
    buf.append(tp.getClassName()).append(": ").append(tp.getMessage()).append(LINE_SEPARATOR);

    subjoinSTEPArray(buf, indent + 2, tp);

    IThrowableProxy[] suppressed = tp.getSuppressed();
    if (suppressed != null) {
      for (IThrowableProxy current : suppressed) {
        recursiveAppend(buf, SUPPRESSED, indent + 2, current);
      }
    }
    recursiveAppend(buf, CAUSED_BY, indent, tp.getCause());
  }

  @Override
  protected void subjoinSTEPArray(StringBuilder buf, int indent, IThrowableProxy tp) {
    StackTraceElementProxy[] elements = tp.getStackTraceElementProxyArray();
    int maxIndex = elements.length - tp.getCommonFrames();

    for (int i = 0; i < maxIndex; i++) {
      StackTraceElementProxy element = elements[i];
      indent(buf, indent);
      buf.append(element);
      buf.append(LINE_SEPARATOR);
    }

    if (tp.getCommonFrames() > 0) {
      indent(buf, indent);
      buf.append("... ").append(tp.getCommonFrames()).append(" Common Frames Omitted").append(LINE_SEPARATOR);
    }
  }
}
