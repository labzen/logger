package cn.labzen.logger.kernel.marker;

import ch.qos.logback.core.util.StringUtil;

public class StatusMarker extends AbstractLimitedMarker {

  private final String text;

  public StatusMarker(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return StringUtil.nullStringToEmpty(text);
  }
}
