package cn.labzen.logger.logback;

import ch.qos.logback.classic.LoggerContext;
import cn.labzen.logger.kernel.LabzenLogger;
import cn.labzen.logger.logback.filter.ForcedFilter;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LabzenLogbackLoggerContext implements ILoggerFactory {

  private static volatile LabzenLogbackLoggerContext INSTANCE;
  private static final Map<String, Logger> LOGGER_CACHE = new ConcurrentHashMap<>();
  private final LoggerContext principal;

  public LabzenLogbackLoggerContext(LoggerContext principal) {
    principal.addTurboFilter(new ForcedFilter());
    this.principal = principal;
    INSTANCE = this;
  }

  @Override
  public Logger getLogger(String name) {
    return LOGGER_CACHE.computeIfAbsent(name, key -> {
      Logger original = principal.getLogger(key);
      return new LabzenLogger(original);
    });
  }

  public LoggerContext getContext() {
    return principal;
  }

  public static LabzenLogbackLoggerContext instance() {
    return INSTANCE;
  }
}
