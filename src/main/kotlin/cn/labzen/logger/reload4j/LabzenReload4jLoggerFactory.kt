package cn.labzen.logger.reload4j

import cn.labzen.logger.kernel.LabzenLogger
import org.slf4j.ILoggerFactory
import org.slf4j.Logger

@Suppress("unused")
class LabzenReload4jLoggerFactory(private val principal: ILoggerFactory) : ILoggerFactory {

  override fun getLogger(name: String?): Logger {
    val original: Logger = principal.getLogger(name)
    return LabzenLogger(original)
  }
}
