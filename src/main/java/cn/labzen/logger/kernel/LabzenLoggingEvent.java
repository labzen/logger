package cn.labzen.logger.kernel;

import ch.qos.logback.core.util.StringUtil;
import cn.labzen.logger.kernel.enums.CodeTypes;
import cn.labzen.logger.kernel.tile.handle.MessagePatternTileManager;
import org.slf4j.event.DefaultLoggingEvent;
import org.slf4j.event.Level;

import java.util.Arrays;
import java.util.stream.Collectors;

import static ch.qos.logback.core.util.StringUtil.nullStringToEmpty;

public class LabzenLoggingEvent extends DefaultLoggingEvent {

  private static final String LINE_SEPARATOR = System.lineSeparator();
  private static final String FRAME_LINE_START_PREFIX = "┌──────────── ====== Lang: ";
  private static final String FRAME_LINE_START_SUFFIX = " ====== ─────────────────────────────────────────────────────────────────";
  private static final String FRAME_PREFIX = "│ ";
  private static final String FRAME_LINE_END = "└───────────────────────────────────────────────────────────────────────────────────────────────────────";

  private final LabzenLogger logger;

  private CodeTypes codeType;
  private String codeText;

  public LabzenLoggingEvent(Level level, LabzenLogger logger) {
    super(level, logger);
    this.logger = logger;
  }

  public void setCodeType(CodeTypes codeType) {
    this.codeType = codeType;
  }

  public void setCodeText(String codeText) {
    this.codeText = codeText;
  }

  @Override
  public String getMessage() {
    String message = nullStringToEmpty(logger.getMessagePrefix()) +
                     MessagePatternTileManager.transform(nullStringToEmpty(super.getMessage()), super.getArguments());

    if (codeType == null) {
      return message;
    } else {
      return genCodeMessage(message);
    }
  }

  private String genCodeMessage(String message) {
    return message +
           "\n" +
           FRAME_LINE_START_PREFIX +
           codeType.getText() +
           FRAME_LINE_START_SUFFIX +
           "\n" +
           formatCodeText() +
           "\n" +
           FRAME_LINE_END;
  }

  private String formatCodeText() {
    if (StringUtil.isNullOrEmpty(codeText)) {
      return "";
    }

    return Arrays.stream(codeText.split("\\R"))
                 .map(line -> FRAME_PREFIX + line)
                 .collect(Collectors.joining(LINE_SEPARATOR));
    //return codeText.replaceAll(LAST_CRLF_REGEX, "").replaceAll(LINE_SEPARATOR, FRAME_PREFIX_WITH_CRLF);
  }
}
