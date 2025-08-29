package cn.labzen.logger.logback;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.LogbackServiceProvider;
import cn.labzen.logger.logback.pattern.conversion.HighlighterConverter;
import cn.labzen.logger.logback.pattern.conversion.IdentifiableLoggerConverter;
import cn.labzen.logger.logback.pattern.conversion.IndentedThrowableProxyConverter;
import org.slf4j.ILoggerFactory;

public class LabzenLogbackServiceProvider extends LogbackServiceProvider {

  private ILoggerFactory labzenLoggerFactory;

  @Override
  public void initialize() {
    enhancePatternLayout();

    super.initialize();

    LoggerContext principalContext = (LoggerContext) super.getLoggerFactory();
    labzenLoggerFactory = new LabzenLogbackLoggerContext(principalContext);
  }

  @Override
  public ILoggerFactory getLoggerFactory() {
    return labzenLoggerFactory;
  }

  private void enhancePatternLayout() {
    // 加入自定义 Conversion Word
    // 重新分配色彩的日志级别
    PatternLayout.DEFAULT_CONVERTER_MAP.put("showy", HighlighterConverter.class.getName());
    PatternLayout.DEFAULT_CONVERTER_MAP.put("highlighter", HighlighterConverter.class.getName());
    // 更短的logger类显示，暂时不建议使用
    PatternLayout.DEFAULT_CONVERTER_MAP.put("briefLogger", IdentifiableLoggerConverter.class.getName());
    PatternLayout.DEFAULT_CONVERTER_MAP.put("brief", IdentifiableLoggerConverter.class.getName());
    PatternLayout.DEFAULT_CONVERTER_MAP.put("bl", IdentifiableLoggerConverter.class.getName());
    // 更直观的异常显示
    PatternLayout.DEFAULT_CONVERTER_MAP.put("thrown", IndentedThrowableProxyConverter.class.getName());
    PatternLayout.DEFAULT_CONVERTER_MAP.put("newEx", IndentedThrowableProxyConverter.class.getName());
    PatternLayout.DEFAULT_CONVERTER_MAP.put("newException", IndentedThrowableProxyConverter.class.getName());
  }
}
