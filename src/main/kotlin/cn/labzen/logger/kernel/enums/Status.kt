package cn.labzen.logger.kernel.enums

enum class Status(val text: String) {

  /**
   * 语义表达完成，严谨的语义可理解未，业务/逻辑无异常的执行完毕，但结果正确/成功与否未知
   */
  DONE("⚑ DONE"),

  /**
   * 语义表达成功（前提肯定是完成[DONE]）
   */
  SUCCESS("✔ SUCCESS"),

  /**
   * 语义表达预知错误（从语义上如需描述异常情况，建议使用[FAILED]）
   */
  WRONG("✘ WRONG"),

  /**
   * 语义表达业务/逻辑失败或产生异常（可能未执行完或无结果，一般记录可控异常）
   */
  FAILED("☠ FAILED"),

  /**
   * 语义表达程序正在等待其他数据或指令，时间上的概念，比如多线程
   */
  WAITING("⁙ WAITING"),

  /**
   * 语义表达程序正在等待决定性的状态，条件上的概念，比如下一步结果取决于某个正在计算的值
   */
  PENDING("⁙ PENDING"),

  /**
   * 语义表达一段业务/逻辑的开始，如需更准确的语义描述，可使用[STARTED]
   */
  START("● START"),

  /**
   * 语义表达一段业务/逻辑正在开始，并需要持续一定的时间
   */
  STARTING("● STARTING"),

  /**
   * 语义表达一段业务/逻辑已经开始，在一定的业务场景，等同于[START]
   */
  STARTED("● STARTED"),

  /**
   * 语义表达一段业务/逻辑的结束，如需更准确的语义描述，可使用[ENDED]
   */
  END("■ END"),

  /**
   * 语义表达一段业务/逻辑的正在结束，并需要持续一定的时间
   */
  ENDING("■ ENDING"),

  /**
   * 语义表达一段业务/逻辑已经结束，在一定的业务场景，等同于[END]
   */
  ENDED("■ ENDED"),

  /**
   * 语义表达一段业务/逻辑的中间短暂记录
   */
  PAUSE("‖ PAUSE"),

  /**
   * 语义表达一段业务/逻辑的顺利完成，如需更准确的语义描述，可使用[COMPLETED]
   */
  COMPLETE("◆ COMPLETE"),

  /**
   * 语义表达一段业务/逻辑正在完成，并需要持续一定的时间
   */
  COMPLETING("◆ COMPLETING"),

  /**
   * 语义表达一段业务/逻辑已经顺利完成，在一定的业务场景，等同于[COMPLETE]
   */
  COMPLETED("◆ COMPLETED"),

  /**
   * 语义表达对某个日志做下标记（普通），可对应IMPORTANT
   */
  NOTE("✎ NOTE"),

  /**
   * 语义表达提醒、警示的作用
   */
  REMIND("✪ REMIND"),

  /**
   * 语义表达日志标记为重要，可对应NOTE
   */
  IMPORTANT("‼ IMPORTANT"),

  /**
   * 语义表达日志标记为危险，可提醒代码运行至较危险的逻辑或将要移除的功能
   */
  DANGER("☢ DANGER"),

  /**
   * 语义表达日志所涉及代码的计时
   */
  TIMER("◔ TIMER"),

  /**
   * 语义表达相关代码为测试作用
   */
  TEST("⚒ TEST"),

  /**
   * 语义表达相关代码存在未完成的内容
   */
  TODO("✍ TODO"),

  /**
   * 语义表达相关代码可能存在问题，需要被修复，用于部分不确认分支代码的进入
   */
  FIXME("✠ FIXME"),

  /**
   * 语义表达相关代码为临时存在，提醒注意及时调整
   */
  TEMP("♻ TEMP"),
}
