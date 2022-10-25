package cn.labzen.logger.logback.filter

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.turbo.TurboFilter
import ch.qos.logback.core.spi.FilterReply
import cn.labzen.logger.kernel.marker.ForcedMarker
import cn.labzen.logger.kernel.marker.LabzenMarkerWrapper
import org.slf4j.Marker

class ForcedFilter : TurboFilter() {

  override fun decide(
    marker: Marker?,
    logger: Logger?,
    level: Level?,
    format: String?,
    params: Array<out Any>?,
    t: Throwable?
  ): FilterReply {
    return marker?.let {
      when (it) {
        is LabzenMarkerWrapper -> if (it.forced != null) FilterReply.ACCEPT else FilterReply.NEUTRAL
        is ForcedMarker -> FilterReply.ACCEPT
        else -> FilterReply.NEUTRAL
      }
    } ?: FilterReply.NEUTRAL
  }
}
