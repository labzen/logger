package cn.labzen.logger.kernel

import cn.labzen.logger.Loggers
import cn.labzen.logger.logback.LabzenLogbackServiceProvider
import cn.labzen.logger.reload4j.LabzenReload4jServiceProvider
import org.slf4j.ILoggerFactory
import org.slf4j.IMarkerFactory
import org.slf4j.spi.MDCAdapter
import org.slf4j.spi.SLF4JServiceProvider

class LabzenLoggerServiceProvider : SLF4JServiceProvider {

  private val actualServiceProvider: SLF4JServiceProvider

  init {
    Loggers.detectLoggerImplements()

    if (Loggers.isLogbackPresent) {
      actualServiceProvider = LabzenLogbackServiceProvider()
    } else if (Loggers.isReload4jPresent) {
      actualServiceProvider = LabzenReload4jServiceProvider()
    } else {
      throw RuntimeException("没有Logback或Reload4j的依赖")
    }

    Loggers.disableIllegalReflectiveWarning()
  }

  override fun getLoggerFactory(): ILoggerFactory =
    actualServiceProvider.loggerFactory

  override fun getMarkerFactory(): IMarkerFactory =
    actualServiceProvider.markerFactory

  override fun getMDCAdapter(): MDCAdapter =
    actualServiceProvider.mdcAdapter

  override fun getRequestedApiVersion(): String =
    actualServiceProvider.requestedApiVersion

  override fun initialize() {
    actualServiceProvider.initialize()
  }
}
