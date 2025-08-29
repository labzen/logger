package cn.labzen.logger.logback;

import ch.qos.logback.classic.LoggerContext;
import cn.labzen.logger.kernel.LabzenLogger;
import cn.labzen.logger.logback.filter.ForcedFilter;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class LabzenLogbackLoggerContext implements ILoggerFactory {

  private static LabzenLogbackLoggerContext INSTANCE;
  private final LoggerContext principal;

  public LabzenLogbackLoggerContext(LoggerContext principal) {
    INSTANCE = this;
    this.principal = principal;

    principal.addTurboFilter(new ForcedFilter());
  }

  @Override
  public Logger getLogger(String name) {
    Logger original = principal.getLogger(name);
    return new LabzenLogger(original);
  }

  public LoggerContext getContext() {
    return principal;
  }

  public static LabzenLogbackLoggerContext instance() {
    return INSTANCE;
  }
}
