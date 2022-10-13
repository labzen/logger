package cn.labzen.logger.config

import cn.labzen.meta.configuration.annotation.Configured
import cn.labzen.meta.configuration.annotation.Item

@Configured(namespace = "logger")
interface LoggerConfiguration {

  /**
   * 输出meta信息，除 all, none 外，其他值可使用英文逗号分隔多选，有错误值则忽略
   * - all 输出全部（默认）
   * - none 不输出
   * - comp 输出组件信息
   * - env 输出系统环境变量
   * - device 输出系统级硬件信息
   */
  @Item(defaultValue = "all")
  fun outputMeta(): String

}
