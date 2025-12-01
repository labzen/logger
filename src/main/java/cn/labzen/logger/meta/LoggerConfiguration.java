package cn.labzen.logger.meta;

import cn.labzen.meta.configuration.annotation.Configured;
import cn.labzen.meta.configuration.annotation.Item;

@Configured(namespace = "logger")
public interface LoggerConfiguration {

  /**
   * 是否打印Labzen Banner图，以及使用的组件信息
   * <p>
   * 该部分信息通过 System.out.println() 打印
   */
  @Item(path = "print-banner", required = false, defaultValue = "true")
  boolean printBanner();

  /**
   * 是否打印业务项目的相关信息
   * <p>
   * 该部分信息会保存到日志文件中
   */
  @Item(path = "print-project-info", required = false, defaultValue = "true")
  boolean printProjectInformation();

  /**
   * 是否打印主机硬件及系统信息，生产环境慎用
   * <p>
   * 该部分信息通过 System.out.println() 打印
   */
  @Item(path = "print-system-info", required = false, defaultValue = "false")
  boolean printSystemInformation();

  /**
   * 日志输出时，如果包含 marker 信息，在marker后添加的分隔符
   */
  @Item(path = "marker-tailed", required = false, defaultValue = "-")
  String markerTailed();
}
