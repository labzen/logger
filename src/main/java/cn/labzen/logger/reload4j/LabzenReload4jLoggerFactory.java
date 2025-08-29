package cn.labzen.logger.reload4j;

import cn.labzen.logger.kernel.LabzenLogger;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class LabzenReload4jLoggerFactory implements ILoggerFactory {

  private final ILoggerFactory principal;

  public LabzenReload4jLoggerFactory(ILoggerFactory principal) {
    this.principal = principal;
  }

  @Override
  public Logger getLogger(String name) {
    Logger original = principal.getLogger(name);
    return new LabzenLogger(original);
  }
}
