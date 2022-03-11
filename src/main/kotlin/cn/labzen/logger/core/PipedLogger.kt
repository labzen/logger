package cn.labzen.logger.core

import ch.qos.logback.classic.Logger
import cn.labzen.logger.core.marker.CodeMarker
import cn.labzen.logger.core.marker.MarkerWrapper
import cn.labzen.logger.core.marker.TagMarker
import org.slf4j.Marker
import org.slf4j.event.Level
import java.util.function.Supplier

class PipedLogger(private val logger: Logger, private val level: Level) {

  private var decided: Boolean? = null
  private var scene: Scene? = null
  private var tags: List<String>? = null


  /**
   * 强制打印日志，忽略日志级别
   *
   * **!! 暂未实现**
   * @param forceCondition 如果为false，则force失效
   */
  fun force(forceCondition: Boolean): PipedLogger {
    return this
  }

  /**
   * 断言，当断言不成立时，不打印日志
   */
  fun decide(decided: Boolean): PipedLogger {
    this.decided = decided
    return this
  }

  /**
   * 等到函数体执行完后打印日志
   *
   * **!! 暂未实现**
   */
  fun wait(block: () -> Unit): PipedLogger {
    return this
  }

  /**
   * 对日志加标签
   */
  fun tag(vararg tags: String): PipedLogger {
    this.tags = tags.asList()
    return this
  }

  /**
   * 对日志打印计数
   *
   * **!! 暂未实现** 可考虑配合tag做区分计数
   */
  fun counting(): PipedLogger {
    return this
  }

  /**
   * 增加场景辅助标识
   */
  fun scene(scene: Scene): PipedLogger {
    this.scene = scene
    return this
  }

  /**
   * 提供阶段性日志，整个阶段过程中，可统计执行时长等信息
   *
   * **!! 暂未实现**
   */
  fun phaseStart(): PipedLogger {
    return this
  }

  fun phasePause(): PipedLogger {
    return this
  }

  fun phaseEnd(): PipedLogger {
    return this
  }

  private fun logMsg(marker: Marker, msg: String?, throwable: Throwable?) {
    when (level) {
      Level.TRACE -> if (logger.isTraceEnabled) {
        logger.trace(marker, msg, throwable)
      }
      Level.DEBUG -> if (logger.isDebugEnabled) {
        logger.debug(marker, msg, throwable)
      }
      Level.INFO -> if (logger.isInfoEnabled) {
        logger.info(marker, msg, throwable)
      }
      Level.WARN -> if (logger.isWarnEnabled) {
        logger.warn(marker, msg, throwable)
      }
      Level.ERROR -> if (logger.isErrorEnabled) {
        logger.error(marker, msg, throwable)
      }
    }
  }

  /**
   * 按JSON的既定格式输出（不提供格式化），无法向[log]方法那样定制格式
   */
  @JvmOverloads
  fun logJson(msg: String? = null, json: String) {
    if (decided == false) {
      return
    }

    val marker = MarkerWrapper(
      this.scene?.marker,
      this.tags?.let { TagMarker(it) },
      CodeMarker(CodeMarker.CodeType.JSON, json)
    )
    logMsg(marker, msg, null)
  }

  /**
   * 按XML的既定格式输出（不提供格式化），无法向[log]方法那样定制格式
   */
  @JvmOverloads
  fun logXml(msg: String? = null, xml: String) {
    if (decided == false) {
      return
    }

    val marker = MarkerWrapper(
      this.scene?.marker,
      this.tags?.let { TagMarker(it) },
      CodeMarker(CodeMarker.CodeType.XML, xml)
    )
    logMsg(marker, msg, null)
  }

  /**
   * 打印日志文本
   */
  fun log(msg: String) {
    if (decided == false) {
      return
    }

    val marker = MarkerWrapper(this.scene?.marker, this.tags?.let { TagMarker(it) })
    logMsg(marker, msg, null)
  }

  /**
   * 打印异常日志
   */
  fun logError(throwable: Throwable, msg: String) {
    if (decided == false) {
      return
    }

    val marker = MarkerWrapper(this.scene?.marker, this.tags?.let { TagMarker(it) })
    logMsg(marker, msg, throwable)
  }

  /**
   * 打印日志模板，等待日志的参数计算，有了返回值然后打印日志
   *
   * 日志模板中placeholder支持样式：
   * 1. {} - 默认占位，如果有多个，则按照给定参数出现的顺序，依次替换模板中的占位
   * 2. {0} - 指参占位，顺序占位，在存在有多个参数List<Object>情况下，从0开始指定占位在参数集合中的位置
   * 3. {param_name} - 指参占位，参数名占位，给定的参数需要是Map<String, Object>，对应参数名替换模板中的占位，param_name必须以字母开头
   * 4. {@number_0.00} - 格式化占位，数字格式化占位，按给定的pattern参数（具体格式参考DecimalFormat类），格式化输出。0代表一个数字，#代表一个数字但不补零。
   *    例如：double x = 16.7; {@number_0} = "16"; {@number_0.0} = "16.7"; {@number_0.00} = "16.70"; {@number_000.##} = "016.7";
   * 5. {@date_yyyy-MM-dd} - 格式化占位，日期格式化占位，按给定的Date等日期时间类型参数，格式化输出。支持Date, LocalDate, LocalTime, LocalDateTime，毫秒数等类型。
   *    例如：{@date_yyyy-MM} = "2021-01"
   * 6. {@wrap_[]} - 函数式占位，将参数使用给定的字符包裹输出。这里wrap是函数，后边带单引号，内部作为包裹参数，包裹参数为2位固定长度的字符串。
   *    例如：String x = "123"; {@wrap_()} = "(123)"; {@wrap_\{\}} = "{123}"; （大括号，单引号，双引号需要转义）
   * 7. {@whether_yes,no} - 函数式占位，仅适用于boolean类型的参数。这里whether是函数，后边带单引号，内部两个参数，用逗号分隔，
   *    不需要空格，当参数为true时，输出第一个参数，否则输出第二个参数。 例如：boolean x = false; {@whether yes,no} = "no"
   * 8. {@width_} - 位宽指定，可指定两个数字值，使用逗号分隔，不需要空格，第一个数字代表最小位宽，即保证日志打印的参数至少占用多少个字符，
   *    如果实际占用字符超出这个设置，等效于忽略该数值。第二个数字代表最大位宽（可忽略），即保证日志打印的参数最多占用多少个字符，
   *    如果超出，则截断。
   *    例如：String x = "1234567890"; {@width_5} = “1234567890”; {@width_12} = "1234567890  "; {@width_0,5} = "12345"
   *
   * 按上述规则，分为1默认占位，2指参占位，3格式化占位，4函数占位，5位宽
   *
   * 除默认占位，其他四种占位，可组合使用，但需要按照顺序出现，相同的占位类型，不需要关心出现的顺序，但会按照顺序去处理。例如：
   * - {price@number_0.0@wrap_||@width_20} 表示取key为price的number数值，格式化为十进制小数点后保留1位的字符串，
   *    然后计算16位的hash值大写，并用两个|包裹起来，输出时保证所占宽度为20位。示例结果为："[EC9BC01D9A00E0A8]  "
   * - {3@whether_是,否@wrap_<>} 表示取下标为3的boolean值，如果为true，输出是。示例结果为："<是>"
   *
   * @param supplier 返回日志参数值，可以为：
   * 1. Map<String, Object>（日志模板的placeholder需要指定对应的参数名），
   * 2. List<Object>（按顺序对应日志模板中的placeholder）
   * 3. 其他Object类型（只能对应日志模板中的一个参数placeholder）
   */
  fun logCalculated(msgWithPattern: String, supplier: Supplier<Any?>) {
    if (decided == false) {
      return
    }

    val args = supplier.get()

    logArguments(msgWithPattern, args)
  }

  /**
   * 打印日志模板，等待日志的参数计算，有了返回值然后打印日志
   *
   * 日志模板中placeholder支持样式：
   * 1. {} - 默认占位，如果有多个，则按照给定参数出现的顺序，依次替换模板中的占位
   * 2. {0} - 指参占位，顺序占位，在存在有多个参数List<Object>情况下，从0开始指定占位在参数集合中的位置
   * 3. {param_name} - 指参占位，参数名占位，给定的参数需要是Map<String, Object>，对应参数名替换模板中的占位，param_name必须以字母开头
   * 4. {@number_0.00} - 格式化占位，数字格式化占位，按给定的pattern参数（具体格式参考DecimalFormat类），格式化输出。0代表一个数字，#代表一个数字但不补零。
   *    例如：double x = 16.7; {@number_0} = "16"; {@number_0.0} = "16.7"; {@number_0.00} = "16.70"; {@number_000.##} = "016.7";
   * 5. {@date_yyyy-MM-dd} - 格式化占位，日期格式化占位，按给定的Date等日期时间类型参数，格式化输出。支持Date, LocalDate, LocalTime, LocalDateTime，毫秒数等类型。
   *    例如：{@date_yyyy-MM} = "2021-01"
   * 6. {@wrap_[]} - 函数式占位，将参数使用给定的字符包裹输出。这里wrap是函数，后边带单引号，内部作为包裹参数，包裹参数为2位固定长度的字符串。
   *    例如：String x = "123"; {@wrap_()} = "(123)"; {@wrap_\{\}} = "{123}"; （大括号，单引号，双引号需要转义）
   * 7. {@whether_yes,no} - 函数式占位，仅适用于boolean类型的参数。这里whether是函数，后边带单引号，内部两个参数，用逗号分隔，
   *    不需要空格，当参数为true时，输出第一个参数，否则输出第二个参数。 例如：boolean x = false; {@whether yes,no} = "no"
   * 8. {@width_} - 位宽指定，可指定两个数字值，使用逗号分隔，不需要空格，第一个数字代表最小位宽，即保证日志打印的参数至少占用多少个字符，
   *    如果实际占用字符超出这个设置，等效于忽略该数值。第二个数字代表最大位宽（可忽略），即保证日志打印的参数最多占用多少个字符，
   *    如果超出，则截断。
   *    例如：String x = "1234567890"; {@width_5} = “1234567890”; {@width_12} = "1234567890  "; {@width_0,5} = "12345"
   *
   * 按上述规则，分为1默认占位，2指参占位，3格式化占位，4函数占位，5位宽
   *
   * 除默认占位，其他四种占位，可组合使用，但需要按照顺序出现，相同的占位类型，不需要关心出现的顺序，但会按照顺序去处理。例如：
   * - {price@number_0.0@wrap_||@width_20} 表示取key为price的number数值，格式化为十进制小数点后保留1位的字符串，
   *    然后计算16位的hash值大写，并用两个|包裹起来，输出时保证所占宽度为20位。示例结果为："[EC9BC01D9A00E0A8]  "
   * - {3@whether_是,否@wrap_<>} 表示取下标为3的boolean值，如果为true，输出是。示例结果为："<是>"
   *
   * @param args 日志参数，可以为：
   * 1. Object类型集合，一个或多个
   * 2. 如果只有一个参数，并且为List<Object>，则按顺序对应日志模板中的placeholder
   * 3. 如果只有一个参数，并且为Map<String, Object>，则：
   *    1 日志模板中只有一个placeholder，并且没有指定key取值，则视为Object类型单个参数输出；
   *    2 否则，等同于参数Map传入，按对应日志模板中的placeholder参数名称对应输出
   */
  fun logArguments(msgWithPattern: String, vararg args: Any?) {
    if (decided == false) {
      return
    }

    val marker = MarkerWrapper(this.scene?.marker, this.tags?.let { TagMarker(it) })
    when (level) {
      Level.TRACE -> if (logger.isTraceEnabled) {
        logger.trace(marker, msgWithPattern, *args)
      }
      Level.DEBUG -> if (logger.isDebugEnabled) {
        logger.debug(marker, msgWithPattern, *args)
      }
      Level.INFO -> if (logger.isInfoEnabled) {
        logger.info(marker, msgWithPattern, *args)
      }
      Level.WARN -> if (logger.isWarnEnabled) {
        logger.warn(marker, msgWithPattern, *args)
      }
      Level.ERROR -> if (logger.isErrorEnabled) {
        logger.error(marker, msgWithPattern, *args)
      }
    }
  }
}
