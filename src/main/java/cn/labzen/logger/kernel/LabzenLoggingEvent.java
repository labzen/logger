package cn.labzen.logger.kernel;

import ch.qos.logback.core.util.StringUtil;
import cn.labzen.logger.kernel.enums.CodeTypes;
import cn.labzen.logger.kernel.tile.handle.MessagePatternTileManager;
import org.slf4j.event.DefaultLoggingEvent;
import org.slf4j.event.Level;

import java.util.Arrays;
import java.util.stream.Collectors;

import static ch.qos.logback.core.util.StringUtil.nullStringToEmpty;

/**
 * Labzen日志事件，扩展SLF4J的DefaultLoggingEvent，提供增强功能。
 *
 * <p>主要增强功能：
 * <ul>
 *   <li>消息前缀注入 - 从Logger获取当前前缀并注入到消息</li>
 *   <li>Tile占位符处理 - 支持高级占位符格式（格式化、包裹、条件判断等）</li>
 *   <li>代码块输出 - 支持JSON/XML/YAML等格式的带边框代码输出</li>
 * </ul>
 *
 * <p>消息处理流程：
 * <ol>
 *   <li>获取Logger的前缀并拼接</li>
 *   <li>通过Tile系统处理消息模板中的占位符</li>
 *   <li>如有codeType，则包装成带边框的代码块格式</li>
 * </ol>
 *
 * @see MessagePatternTileManager
 * @see CodeTypes
 */
public class LabzenLoggingEvent extends DefaultLoggingEvent {

  /** 平台相关的行分隔符 */
  private static final String LINE_SEPARATOR = System.lineSeparator();

  /** 代码块起始边框前缀 */
  private static final String FRAME_LINE_START_PREFIX = "┌──────────── ====== Lang: ";

  /** 代码块起始边框后缀（用于填充长度） */
  private static final String FRAME_LINE_START_SUFFIX = " ====== ─────────────────────────────────────────────────────────────────";

  /** 代码块每行的前缀字符 */
  private static final String FRAME_PREFIX = "│ ";

  /** 代码块结束边框 */
  private static final String FRAME_LINE_END = "└───────────────────────────────────────────────────────────────────────────────────────────────────────";

  /** 持有此事件的Logger引用，用于获取前缀配置 */
  private final LabzenLogger logger;

  /** 代码类型，用于标识代码块的编程语言 */
  private CodeTypes codeType;

  /** 代码文本内容 */
  private String codeText;

  /**
   * 构造方法
   *
   * @param level  日志级别
   * @param logger 持有此事件的LabzenLogger引用
   */
  public LabzenLoggingEvent(Level level, LabzenLogger logger) {
    super(level, logger);
    this.logger = logger;
  }

  /**
   * 设置代码类型标识
   *
   * @param codeType 代码类型枚举
   */
  public void setCodeType(CodeTypes codeType) {
    this.codeType = codeType;
  }

  /**
   * 设置代码文本内容
   *
   * @param codeText 代码文本
   */
  public void setCodeText(String codeText) {
    this.codeText = codeText;
  }

  /**
   * 获取处理后的消息文本
   *
   * <p>处理流程：
   * <ol>
   *   <li>拼接Logger的前缀（如果有）</li>
   *   <li>通过Tile系统处理模板占位符</li>
   *   <li>如有codeType，包装成带边框的代码块</li>
   * </ol>
   *
   * @return 处理后的最终消息文本
   */
  @Override
  public String getMessage() {
    // 1. 拼接前缀和处理占位符
    String message = nullStringToEmpty(logger.getMessagePrefix()) +
                     MessagePatternTileManager.transform(nullStringToEmpty(super.getMessage()), super.getArguments());

    // 2. 如果没有代码类型，直接返回处理后的消息
    if (codeType == null) {
      return message;
    } else {
      // 3. 有代码类型，包装成带边框的代码块格式
      return genCodeMessage(message);
    }
  }

  /**
   * 生成带边框的代码块消息
   *
   * <p>输出格式：
   * <pre>
   * [message]
   * ┌──────────── ====== [Lang] ====== ──────────────────────────────
   * │ [code line 1]
   * │ [code line 2]
   * │ ...
   * └────────────────────────────────────────────────────────────────
   * </pre>
   *
   * @param message 原始消息文本（显示在边框上方）
   * @return 格式化后的代码块字符串
   */
  private String genCodeMessage(String message) {
    return String.format("%s\n%s%s%s\n%s\n%s",
        message,
        FRAME_LINE_START_PREFIX,
        codeType.getText(),
        FRAME_LINE_START_SUFFIX,
        formatCodeText(),
        FRAME_LINE_END);
  }

  /**
   * 格式化代码文本，为每行添加边框前缀
   *
   * <p>使用平台相关的行分隔符分割文本，每行添加"│ "前缀
   * <p>例如：原始文本"line1\nline2"变为：
   * <pre>
   * │ line1
   * │ line2
   * </pre>
   *
   * @return 格式化后的代码行字符串
   */
  private String formatCodeText() {
    if (StringUtil.isNullOrEmpty(codeText)) {
      return "";
    }

    // 使用正则\R匹配任意行分隔符（\n, \r\n, \r）
    return Arrays.stream(codeText.split("\\R"))
                 .map(line -> FRAME_PREFIX + line)
                 .collect(Collectors.joining(LINE_SEPARATOR));
  }
}
