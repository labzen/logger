@file:Suppress("unused")

package cn.labzen.logger.core

import ch.qos.logback.core.pattern.color.ANSIConstants.*
import cn.labzen.logger.core.marker.SceneMarker

enum class Scene(val marker: SceneMarker) {

  /**
   * 表达完成
   */
  DONE(SceneMarker("☺ DONE", GREEN_FG)),

  /**
   * 表达成功（肯定是完成）
   */
  SUCCESS(SceneMarker("✔ SUCCESS", BOLD + GREEN_FG)),

  /**
   * 表达可知错误（非异常）
   */
  WRONG(SceneMarker("✘ WRONG", RED_FG)),

  /**
   * 表达失败（可能未执行完或无结果，可控）
   */
  FAILED(SceneMarker("☹ FAILED", BOLD + RED_FG)),

  /**
   * 表达程序正在等待，时间上的概念，比如多线程
   */
  WAITING(SceneMarker("❃ WAITING", CYAN_FG)),

  /**
   * 表达程序正在等待，条件上的概念，比如下一步结果取决于某个正在计算的值
   */
  PENDING(SceneMarker("❁ PENDING", CYAN_FG)),

  /**
   * 表达一段逻辑的开始，最好配合TAG使用
   */
  START(SceneMarker("● START", MAGENTA_FG)),

  /**
   * 表达一段逻辑的结束，最好配合TAG使用
   */
  END(SceneMarker("■ END", MAGENTA_FG)),

  /**
   * 表达一段逻辑的中间短暂记录，最好配合TAG使用
   */
  PAUSE(SceneMarker("‖ PAUSE", MAGENTA_FG)),

  /**
   * 表达一段逻辑的顺利完成，模糊等同于END
   */
  COMPLETE(SceneMarker("◆ COMPLETE", BOLD + MAGENTA_FG)),

  /**
   * 表达对某个日志做下标记（普通），可对应IMPORTANT
   */
  NOTE(SceneMarker("❤ NOTE", MAGENTA_FG)),

  /**
   * 表达日志标记为重要，可对应NOTE
   */
  IMPORTANT(SceneMarker("☢ IMPORTANT", BOLD + MAGENTA_FG)),

  /**
   * 表达提醒、警示的作用
   */
  REMIND(SceneMarker("✪ REMIND", MAGENTA_FG)),

  /**
   * 表达日志所涉及代码的计时
   */
  TIMER(SceneMarker("◔ TIMER", BLUE_FG)),

  /**
   * 表达相关代码为测试作用
   */
  TEST(SceneMarker("✄ TEST", BLUE_FG)),

  /**
   * 表达相关代码存在未完成的内容
   */
  TODO(SceneMarker("✈ TODO", BOLD + CYAN_FG)),

  /**
   * 表达相关代码可能存在问题，需要被修复，用于部分不确认分支代码的进入
   */
  FIXME(SceneMarker("✙ FIXME", BOLD + CYAN_FG))
}
