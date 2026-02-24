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
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("showy", HighlighterConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("highlighter", HighlighterConverter::new);
    // 更短的logger类显示，暂时不建议使用
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("briefLogger", IdentifiableLoggerConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("brief", IdentifiableLoggerConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("bl", IdentifiableLoggerConverter::new);
    // 更直观的异常显示
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("thrown", IndentedThrowableProxyConverter::new);
    // todo 下面这两个可能有什么问题，还需要再验证，暂时先注掉
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("newEx", IndentedThrowableProxyConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("newException", IndentedThrowableProxyConverter::new);
  }
}
