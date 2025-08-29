package cn.labzen.logger.logback.pattern.conversion;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;

/**
 * Color的说明见下：
 * <p>
 * 背景颜色范围:40----49
 * > 40:黑 41:深红 42:绿 43:黄色 44:蓝色 45:紫色 46:深绿 47:白色
 * <p>
 * 字颜色:30----39
 * > 30:黑 31:红 32:绿 33:黄 34:蓝色 35:紫色 36:深绿 37:白色
 * <p>
 * ===ANSI控制码的说明===
 * <ul>
 * <li> \33[0m 关闭所有属性
 * <li> \33[1m 设置高亮度
 * <li> \33[4m 下划线
 * <li> \33[5m 闪烁
 * <li> \33[7m 反显
 * <li> \33[8m 消隐
 * <li> \33[30m -- \33[37m 设置前景色
 * <li> \33[40m -- \33[47m 设置背景色
 * <li> \33[nA 光标上移n行
 * <li> \33[nB 光标下移n行
 * <li> \33[nC 光标右移n行
 * <li> \33[nD 光标左移n行
 * <li> \33[y;xH设置光标位置
 * <li> \33[2J 清屏
 * <li> \33[K 清除从光标到行尾的内容
 * <li> \33[s 保存光标位置
 * <li> \33[u 恢复光标位置
 * <li> \33[?25l 隐藏光标
 * <li> \33[?25h 显示光标
 * </ul>
 */
public class HighlighterConverter extends CompositeConverter<ILoggingEvent> {

  private static final String ESC_END = "\u001B[0;39;49m";
  private static final String ERROR_LEVEL_TEXT = "\u001B[1;4;30;41m";
  private static final String WARN_LEVEL_TEXT = "\u001B[1;30;43m";
  private static final String INFO_LEVEL_TEXT = "\u001B[30;42m";
  private static final String DEBUG_LEVEL_TEXT = "\u001B[30;47m";
  private static final String TRACE_LEVEL_TEXT = "\u001B[30;47m";

  @Override
  protected String transform(ILoggingEvent event, String text) {
    String level;
    if (event.getLevel() == Level.ERROR) {
      level = ERROR_LEVEL_TEXT;
    } else if (event.getLevel() == Level.WARN) {
      level = WARN_LEVEL_TEXT;
    } else if (event.getLevel() == Level.INFO) {
      level = INFO_LEVEL_TEXT;
    } else if (event.getLevel() == Level.DEBUG) {
      level = DEBUG_LEVEL_TEXT;
    } else {
      level = TRACE_LEVEL_TEXT;
    }
    return level + text + ESC_END;
  }
}
