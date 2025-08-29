package cn.labzen.logger.meta;

import cn.labzen.meta.component.DeclaredComponent;

public class LoggerMeta implements DeclaredComponent {

  @Override
  public String mark() {
    return "Labzen-Logger";
  }

  @Override
  public String packageBased() {
    return "cn.labzen.logger";
  }

  @Override
  public String description() {
    return "基于SLF4j日志门户，封装Logback或Reload4j(Log4j2)提供增强的日志功能";
  }
}
