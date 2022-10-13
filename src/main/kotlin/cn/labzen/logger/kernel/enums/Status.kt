package cn.labzen.logger.kernel.enums

import ch.qos.logback.core.pattern.color.ANSIConstants

enum class Status(val text: String, val color: String) {

  /**
   * 语义表达完成，严谨的语义可理解未，业务/逻辑无异常的执行完毕，但结果正确/成功与否未知
   */
  DONE("⚑ DONE", ANSIConstants.GREEN_FG),

  /**
   * 语义表达成功（前提肯定是完成[DONE]）
   */
  SUCCESS("✔ SUCCESS", ANSIConstants.BOLD + ANSIConstants.GREEN_FG),

  /**
   * 语义表达预知错误（从语义上如需描述异常情况，建议使用[FAILED]）
   */
  WRONG("✘ WRONG", ANSIConstants.RED_FG),

  /**
   * 语义表达业务/逻辑失败或产生异常（可能未执行完或无结果，一般记录可控异常）
   */
  FAILED("☠ FAILED", ANSIConstants.BOLD + ANSIConstants.RED_FG),

  /**
   * 语义表达程序正在等待其他数据或指令，时间上的概念，比如多线程
   */
  WAITING("⁙ WAITING", ANSIConstants.CYAN_FG),

  /**
   * 语义表达程序正在等待决定性的状态，条件上的概念，比如下一步结果取决于某个正在计算的值
   */
  PENDING("⁙ PENDING", ANSIConstants.CYAN_FG),

  /**
   * 语义表达一段业务/逻辑的开始，如需更准确的语义描述，可使用[STARTED]
   */
  START("● START", ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达一段业务/逻辑正在开始，并需要持续一定的时间
   */
  STARTING("● STARTING", ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达一段业务/逻辑已经开始，在一定的业务场景，等同于[START]
   */
  STARTED("● STARTING", ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达一段业务/逻辑的结束，如需更准确的语义描述，可使用[ENDED]
   */
  END("■ END", ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达一段业务/逻辑的正在结束，并需要持续一定的时间
   */
  ENDING("■ END", ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达一段业务/逻辑已经结束，在一定的业务场景，等同于[END]
   */
  ENDED("■ END", ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达一段业务/逻辑的中间短暂记录
   */
  PAUSE("‖ PAUSE", ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达一段业务/逻辑的顺利完成，如需更准确的语义描述，可使用[COMPLETED]
   */
  COMPLETE("◆ COMPLETE", ANSIConstants.BOLD + ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达一段业务/逻辑正在完成，并需要持续一定的时间
   */
  COMPLETING("◆ COMPLETE", ANSIConstants.BOLD + ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达一段业务/逻辑已经顺利完成，在一定的业务场景，等同于[COMPLETE]
   */
  COMPLETED("◆ COMPLETE", ANSIConstants.BOLD + ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达对某个日志做下标记（普通），可对应IMPORTANT
   */
  NOTE("✎ NOTE", ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达提醒、警示的作用
   */
  REMIND("✪ REMIND", ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达日志标记为重要，可对应NOTE
   */
  IMPORTANT("‼ IMPORTANT", ANSIConstants.BOLD + ANSIConstants.MAGENTA_FG),

  /**
   * 语义表达日志标记为危险，可提醒代码运行至较危险的逻辑或将要移除的功能
   */
  DANGER("☢ DANGER", ANSIConstants.BOLD + ANSIConstants.RED_FG),

  /**
   * 语义表达日志所涉及代码的计时
   */
  TIMER("◔ TIMER", ANSIConstants.BLUE_FG),

  /**
   * 语义表达相关代码为测试作用
   */
  TEST("⚒ TEST", ANSIConstants.BLUE_FG),

  /**
   * 语义表达相关代码存在未完成的内容
   */
  TODO("✍ TODO", ANSIConstants.BOLD + ANSIConstants.CYAN_FG),

  /**
   * 语义表达相关代码可能存在问题，需要被修复，用于部分不确认分支代码的进入
   */
  FIXME("✠ FIXME", ANSIConstants.BOLD + ANSIConstants.CYAN_FG),

  /**
   * 语义表达相关代码为临时存在，提醒注意及时调整
   */
  TEMP("♻ TEMP", ANSIConstants.BOLD + ANSIConstants.CYAN_FG)
}
