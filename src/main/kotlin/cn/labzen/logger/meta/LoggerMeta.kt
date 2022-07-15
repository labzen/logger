package cn.labzen.logger.meta

import cn.labzen.meta.component.LabzenComponent

class LoggerMeta : LabzenComponent {
  override fun mark(): String =
    "Labzen-Logger"

  override fun packageBased(): String =
    "cn.labzen.logger"

  override fun description(): String =
    "基于SLF4j日志门户，封装Logback或Log4j2提供增强的日志功能"
}
