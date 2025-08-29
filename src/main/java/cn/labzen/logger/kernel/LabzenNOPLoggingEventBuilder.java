package cn.labzen.logger.kernel;

import org.slf4j.Marker;
import org.slf4j.spi.LoggingEventBuilder;

import java.util.function.Supplier;

public class LabzenNOPLoggingEventBuilder extends LabzenLoggingEventBuilder {

  static final LabzenNOPLoggingEventBuilder SINGLETON = new LabzenNOPLoggingEventBuilder();

  private LabzenNOPLoggingEventBuilder() {
    super(null, null);
  }

  public static LabzenNOPLoggingEventBuilder singleton() {
    return SINGLETON;
  }

  @Override
  public LoggingEventBuilder setCause(Throwable cause) {
    return singleton();
  }

  @Override
  public LoggingEventBuilder addMarker(Marker marker) {
    return singleton();
  }

  @Override
  public LoggingEventBuilder addArgument(Object p) {
    return singleton();
  }

  @Override
  public LoggingEventBuilder addArgument(Supplier<?> objectSupplier) {
    return singleton();
  }

  @Override
  public LoggingEventBuilder addKeyValue(String key, Object value) {
    return singleton();
  }

  @Override
  public LoggingEventBuilder addKeyValue(String key, Supplier<Object> valueSupplier) {
    return singleton();
  }

  @Override
  public LoggingEventBuilder setMessage(String message) {
    return singleton();
  }

  @Override
  public LoggingEventBuilder setMessage(Supplier<String> messageSupplier) {
    return singleton();
  }

  @Override
  public void log() {
  }

  @Override
  public void log(String message) {
  }

  @Override
  public void log(String message, Object arg) {
  }

  @Override
  public void log(String message, Object arg0, Object arg1) {
  }

  @Override
  public void log(String message, Object... args) {
  }

  @Override
  public void log(Supplier<String> messageSupplier) {
  }
}
