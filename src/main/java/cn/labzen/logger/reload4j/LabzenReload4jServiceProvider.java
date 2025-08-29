package cn.labzen.logger.reload4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.reload4j.Reload4jServiceProvider;

public class LabzenReload4jServiceProvider extends Reload4jServiceProvider {

  private ILoggerFactory loggerFactory;

  @Override
  public void initialize() {
    super.initialize();

    ILoggerFactory principalFactory = super.getLoggerFactory();
    loggerFactory = new LabzenReload4jLoggerFactory(principalFactory);
  }

  @Override
  public ILoggerFactory getLoggerFactory() {
    return loggerFactory;
  }
}
