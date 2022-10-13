package cn.labzen.logger.logback.pattern.conversion

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.pattern.CompositeConverter
import ch.qos.logback.core.pattern.color.ANSIConstants.*

/**
 * Color的说明见下：
 *
 * 背景颜色范围:40----49
 * > 40:黑 41:深红 42:绿 43:黄色 44:蓝色 45:紫色 46:深绿 47:白色
 *
 * 字颜色:30----39
 * > 30:黑 31:红 32:绿 33:黄 34:蓝色 35:紫色 36:深绿 37:白色
 *
 * ===ANSI控制码的说明===
 * - \33[0m 关闭所有属性
 * - \33[1m 设置高亮度
 * - \33[4m 下划线
 * - \33[5m 闪烁
 * - \33[7m 反显
 * - \33[8m 消隐
 * - \33[30m -- \33[37m 设置前景色
 * - \33[40m -- \33[47m 设置背景色
 * - \33[nA 光标上移n行
 * - \33[nB 光标下移n行
 * - \33[nC 光标右移n行
 * - \33[nD 光标左移n行
 * - \33[y;xH设置光标位置
 * - \33[2J 清屏
 * - \33[K 清除从光标到行尾的内容
 * - \33[s 保存光标位置
 * - \33[u 恢复光标位置
 * - \33[?25l 隐藏光标
 * - \33[?25h 显示光标
 */
class ColoredLevelConverter : CompositeConverter<ILoggingEvent>() {

  override fun transform(event: ILoggingEvent, text: String?): String =
    when (event.level) {
      Level.ERROR -> ERROR_TEXT
      Level.WARN -> WARN_TEXT
      Level.INFO -> INFO_TEXT
      Level.DEBUG -> DEBUG_TEXT
      else -> TRACE_TEXT
    }

  companion object {
    private const val ANSI_END = "$ESC_START$DEFAULT_FG$ESC_END"

    private const val ERROR_TEXT = "$ESC_START$RED_FG${ESC_END}ERROR$ANSI_END"
    private const val WARN_TEXT = "$ESC_START$YELLOW_FG${ESC_END}WARN$ANSI_END"
    private const val INFO_TEXT = "$ESC_START$GREEN_FG${ESC_END}INFO $ANSI_END"
    private const val DEBUG_TEXT = "$ESC_START$BLUE_FG${ESC_END}DEBUG$ANSI_END"
    private const val TRACE_TEXT = "$ESC_START$CYAN_FG${ESC_END}TRACE$ANSI_END"
  }
}
