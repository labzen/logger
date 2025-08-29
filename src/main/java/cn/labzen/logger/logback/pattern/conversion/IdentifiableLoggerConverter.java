package cn.labzen.logger.logback.pattern.conversion;

import ch.qos.logback.classic.pattern.LoggerConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import cn.labzen.meta.Labzens;
import cn.labzen.meta.component.bean.ComponentMeta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 可进一步缩写类名%logger，比如类路径已知是Spring Boot的，则显示 "SpringBoot@{类名}"
 * <p>
 * TODO 暂时不建议使用，未使用缓存，日志处理会比较慢
 */
public class IdentifiableLoggerConverter extends LoggerConverter {

  private static final String LOGGER_NAMES_PROPERTIES_FILE = "logger-names.properties";
  private static final Map<String, String> PACKAGE_NAMES_MAPPING;

  static {
    PACKAGE_NAMES_MAPPING = tryReadResource();

    // 添加Labzen的组件package路径
    for (ComponentMeta meta : Labzens.getComponentMetas().values()) {
      PACKAGE_NAMES_MAPPING.put(meta.component().packageBased(), meta.component().mark() + "@");
    }
  }

  private static Map<String, String> tryReadResource() {
    Map<String, String> result = tryReadResource1();
    if (result.isEmpty()) {
      result = tryReadResource2();
    }

    return result;
  }

  private static Map<String, String> tryReadResource1() {
    URL resource = IdentifiableLoggerConverter.class.getClassLoader().getResource(LOGGER_NAMES_PROPERTIES_FILE);
    if (resource == null) {
      return Collections.emptyMap();
    }

    try (InputStream is = resource.openStream()) {
      return readResource(is);
    } catch (IOException e) {
      return Collections.emptyMap();
    }
  }

  private static Map<String, String> tryReadResource2() {
    try (InputStream is = IdentifiableLoggerConverter.class.getResourceAsStream(LOGGER_NAMES_PROPERTIES_FILE)) {
      return readResource(is);
    } catch (IOException e) {
      return Collections.emptyMap();
    }
  }

  private static Map<String, String> readResource(InputStream stream) throws IOException {
    Properties properties = new Properties();
    properties.load(stream);

    Map<String, String> map = new HashMap<>();
    for (String name : properties.stringPropertyNames()) {
      map.put(name, properties.getProperty(name));
    }
    return map;
  }

  @Override
  public String convert(ILoggingEvent event) {
    return super.convert(event);
  }

  private String logText(ILoggingEvent event) {
    String fqn = getFullyQualifiedName(event);
    String pkg = lessClassPackagePath(fqn);
    while (!pkg.isEmpty()) {
      if (PACKAGE_NAMES_MAPPING.containsKey(pkg)) {
        return PACKAGE_NAMES_MAPPING.get(pkg) + classRefer(event);
      }

      pkg = lessClassPackagePath(pkg);
    }
    return null;
  }

  private String lessClassPackagePath(String path) {
    int i = path.lastIndexOf('.');
    if (i == -1) {
      return path;
    } else {
      return path.substring(0, i);
    }
  }

  private String classRefer(ILoggingEvent event) {
    String loggerName = event.getLoggerName();
    for (StackTraceElement caller : event.getCallerData()) {
      if (loggerName.equals(caller.getClassName())) {
        return "(" + caller.getFileName() + ":" + caller.getLineNumber() + ")";
      }
    }

    return loggerName.substring(loggerName.lastIndexOf('.') + 1);
  }
}
