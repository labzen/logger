package cn.labzen.logger.logback.pattern.converter.processor

import ch.qos.logback.classic.pattern.EnsureExceptionHandling
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Context
import ch.qos.logback.core.pattern.Converter
import ch.qos.logback.core.pattern.ConverterUtil
import cn.labzen.logger.logback.pattern.converter.IndentedThrowableProxyConverter

class EnsureCustomizeExceptionHandling : EnsureExceptionHandling() {

  override fun process(context: Context, head: Converter<ILoggingEvent>?) {
    requireNotNull(head) {
      // this should never happen
      "cannot process empty chain"
    }
    if (!chainHandlesThrowable(head)) {
      val tail = ConverterUtil.findTail(head)
      val exConverter: Converter<ILoggingEvent> = IndentedThrowableProxyConverter()
      tail.next = exConverter
    }
  }
}
