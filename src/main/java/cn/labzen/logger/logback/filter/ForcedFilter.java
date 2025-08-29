package cn.labzen.logger.logback.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import cn.labzen.logger.kernel.marker.ForcedMarker;
import cn.labzen.logger.kernel.marker.MarkerWrapper;
import org.slf4j.Marker;

public class ForcedFilter extends TurboFilter {

  @Override
  public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
    switch (marker) {
      case MarkerWrapper mw -> {
        if (mw.getForced() != null) {
          return FilterReply.ACCEPT;
        } else {
          return FilterReply.NEUTRAL;
        }
      }
      case ForcedMarker ignored -> {
        return FilterReply.ACCEPT;
      }
      case null, default -> {
        return FilterReply.NEUTRAL;
      }
    }
  }
}
