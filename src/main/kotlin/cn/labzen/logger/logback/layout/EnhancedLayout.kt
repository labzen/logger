package cn.labzen.logger.logback.layout

import ch.qos.logback.classic.PatternLayout
import cn.labzen.logger.config.LabzenLoggerConfiguration
import cn.labzen.logger.logback.pattern.converter.*
import cn.labzen.logger.logback.pattern.converter.processor.EnsureCustomizeExceptionHandling

class EnhancedLayout : PatternLayout() {

  override fun start() {
    defaultConverterMap["x_level"] = ColoredLevelConverter::class.java.name
    defaultConverterMap["x_logger"] = IdentifiableLoggerConverter::class.java.name
    defaultConverterMap["x_scene"] = SceneConverter::class.java.name
    defaultConverterMap["x_tag"] = TagConverter::class.java.name
    defaultConverterMap["x_message"] = PipelineMessageConverter::class.java.name

    this.setPostCompileProcessor(EnsureCustomizeExceptionHandling())
    this.pattern = LabzenLoggerConfiguration.instance.pattern
    super.start()
  }
}
