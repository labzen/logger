package cn.labzen.logger.meta;

import cn.labzen.meta.configuration.annotation.Configured;
import cn.labzen.meta.configuration.annotation.Item;

@Configured(namespace = "logger")
public interface LoggerConfiguration {

  @Item(path = "print-meta", required = false, defaultValue = "false")
  boolean printMetaInformation();
}
