package cn.labzen.logger.meta;

import cn.labzen.meta.configuration.annotation.Configured;
import cn.labzen.meta.configuration.annotation.Item;

@Configured(namespace = "logger")
public interface LoggerConfiguration {

  @Item(path = "print-banner", required = false, defaultValue = "true")
  boolean printBanner();

  @Item(path = "print-project-info", required = false, defaultValue = "true")
  boolean printProjectInformation();

  @Item(path = "print-system-info", required = false, defaultValue = "false")
  boolean printSystemInformation();

  @Item(path = "marker-tailed", required = false, defaultValue = " - ")
  String markerTailed();
}
