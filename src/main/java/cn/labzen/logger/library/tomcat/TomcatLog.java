package cn.labzen.logger.library.tomcat;

import cn.labzen.logger.Loggers;
import cn.labzen.logger.kernel.LabzenLogger;
import org.apache.juli.logging.Log;

public class TomcatLog implements Log {

  private static final String SERVLET_NAME = "TOMCAT";
  private final LabzenLogger logger;

  public TomcatLog(String fqcn) {
    this.logger = Loggers.getLogger(fqcn);
  }

  public TomcatLog() {
    this("<UNKNOWN>");
  }

  private String safeToString(Object o) {
    return o == null ? "null" : o.toString();
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public boolean isFatalEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public void trace(Object o) {
    logger.atTrace().scene(SERVLET_NAME).log(safeToString(o));
  }

  @Override
  public void trace(Object o, Throwable throwable) {
    logger.atTrace().scene(SERVLET_NAME).setCause(throwable).log(safeToString(o));
  }

  @Override
  public void debug(Object o) {
    logger.atDebug().scene(SERVLET_NAME).log(safeToString(o));
  }

  @Override
  public void debug(Object o, Throwable throwable) {
    logger.atDebug().scene(SERVLET_NAME).setCause(throwable).log(safeToString(o));
  }

  @Override
  public void info(Object o) {
    logger.atInfo().scene(SERVLET_NAME).log(safeToString(o));
  }

  @Override
  public void info(Object o, Throwable throwable) {
    logger.atInfo().scene(SERVLET_NAME).setCause(throwable).log(safeToString(o));
  }

  @Override
  public void warn(Object o) {
    logger.atWarn().scene(SERVLET_NAME).log(safeToString(o));
  }

  @Override
  public void warn(Object o, Throwable throwable) {
    logger.atWarn().scene(SERVLET_NAME).setCause(throwable).log(safeToString(o));
  }

  @Override
  public void error(Object o) {
    logger.atError().scene(SERVLET_NAME).log(safeToString(o));
  }

  @Override
  public void error(Object o, Throwable throwable) {
    logger.atError().scene(SERVLET_NAME).setCause(throwable).log(safeToString(o));
  }

  @Override
  public void fatal(Object o) {
    logger.atError().scene(SERVLET_NAME).log(safeToString(o));
  }

  @Override
  public void fatal(Object o, Throwable throwable) {
    logger.atError().scene(SERVLET_NAME).setCause(throwable).log(safeToString(o));
  }
}
