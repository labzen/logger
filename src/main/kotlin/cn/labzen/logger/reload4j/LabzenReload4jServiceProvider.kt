package cn.labzen.logger.reload4j

import org.slf4j.ILoggerFactory
import org.slf4j.reload4j.Reload4jServiceProvider

class LabzenReload4jServiceProvider : Reload4jServiceProvider() {

  private lateinit var labzenLoggerFactory: ILoggerFactory

  override fun initialize() {
    super.initialize()

    val principalFactory = super.getLoggerFactory()
    labzenLoggerFactory = LabzenReload4jLoggerFactory(principalFactory)
  }

  override fun getLoggerFactory(): ILoggerFactory =
    labzenLoggerFactory
}
