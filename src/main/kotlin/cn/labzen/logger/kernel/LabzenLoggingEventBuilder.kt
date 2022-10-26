package cn.labzen.logger.kernel

import cn.labzen.logger.kernel.enums.CodeTypes
import cn.labzen.logger.kernel.enums.Scenes
import cn.labzen.logger.kernel.enums.Status
import cn.labzen.logger.kernel.marker.*
import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.spi.DefaultLoggingEventBuilder
import java.util.function.Supplier

/**
 * TODO 改注释
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
 * 参数值，可以为：
 * 1. Map<String, Object>（日志模板的placeholder需要指定对应的参数名），
 * 2. List<Object>（按顺序对应日志模板中的placeholder）
 * 3. 其他Object类型（只能对应日志模板中的一个参数placeholder）
 */
class LabzenLoggingEventBuilder(logger: Logger, level: Level) : DefaultLoggingEventBuilder(logger, level) {

  private var condition: Boolean = true
  private var forcedMarker: ForcedMarker? = null
  private var tagMarker: TagMarker? = null
  private var sceneMarker: SceneMarker? = null
  private var statusMarker: StatusMarker? = null
  private val le: LabzenLoggingEvent = LabzenLoggingEvent(level, logger)

  private val enabled = when (level) {
    Level.TRACE -> logger.isTraceEnabled
    Level.DEBUG -> logger.isDebugEnabled
    Level.INFO -> logger.isInfoEnabled
    Level.WARN -> logger.isWarnEnabled
    Level.ERROR -> logger.isErrorEnabled
  }

  init {
    loggingEvent = le
  }

  /**
   * 强制打印日志，忽略日志级别，但对于[conditional]条件，并不受影响，即如果condition为false，则绝对不会打印
   */
  fun force(): LabzenLoggingEventBuilder {
    this.forcedMarker = ForcedMarker()
    return this
  }

  /**
   * 日志输出条件，当条件不成立时，不打印日志
   */
  fun conditional(condition: Boolean): LabzenLoggingEventBuilder {
    this.condition = condition
    return this
  }

  /**
   * 日志输出条件，当条件不成立时，不打印日志
   */
  fun conditional(conditionSupplier: Supplier<Boolean>): LabzenLoggingEventBuilder =
    conditional(conditionSupplier.get())

  /**
   * 增加场景辅助标识
   */
  fun scene(scene: Scenes): LabzenLoggingEventBuilder =
    scene(scene.name)

  /**
   * 增加场景辅助标识
   */
  fun scene(scene: String): LabzenLoggingEventBuilder {
    this.sceneMarker = SceneMarker(scene)
    return this
  }

  /**
   * 增加状态辅助标识
   */
  fun status(status: Status): LabzenLoggingEventBuilder {
    this.statusMarker = StatusMarker(status.text)
    return this
  }

  /**
   * 增加状态辅助标识
   */
  fun status(customerText: String): LabzenLoggingEventBuilder {
    this.statusMarker = StatusMarker(customerText)
    return this
  }

  /**
   * 对日志加标签
   */
  fun tag(vararg tags: String): LabzenLoggingEventBuilder {
    this.tagMarker = TagMarker(tags.asList())
    return this
  }

  /**
   * 对日志加标签
   */
  fun tag(tags: Collection<String>): LabzenLoggingEventBuilder {
    this.tagMarker = TagMarker(tags.toList())
    return this
  }

  /**
   * 对日志打印计数，可配合tag与scene做区分计数
   *
   * **!! 暂未实现 !!**
   */
  fun counting(): LabzenLoggingEventBuilder {
    return this
  }

  /**
   * 提供阶段性日志，整个阶段过程中，可统计执行时长等信息
   *
   * **!! 暂未实现 !!**
   */
  fun phaseStart(): LabzenLoggingEventBuilder {
    return this
  }

  /**
   * 提供阶段性日志，整个阶段过程中，可统计执行时长等信息
   *
   * **!! 暂未实现 !!**
   */
  fun phasePause(): LabzenLoggingEventBuilder {
    return this
  }

  /**
   * 提供阶段性日志，整个阶段过程中，可统计执行时长等信息
   *
   * **!! 暂未实现 !!**
   */
  fun phaseEnd(): LabzenLoggingEventBuilder {
    return this
  }

  /**
   * 打印JSON数据
   */
  fun json(text: String): LabzenLoggingEventBuilder {
    le.codeType = CodeTypes.JSON
    le.codeText = text
    return this
  }

  /**
   * 打印XML数据
   */
  fun xml(text: String): LabzenLoggingEventBuilder {
    le.codeType = CodeTypes.XML
    le.codeText = text
    return this
  }

  /**
   * 打印YAML数据
   */
  fun yaml(text: String): LabzenLoggingEventBuilder {
    le.codeType = CodeTypes.YAML
    le.codeText = text
    return this
  }

  private fun addMarkerIfNecessary() {
    if (forcedMarker != null || sceneMarker != null || statusMarker != null || tagMarker != null) {
      addMarker(LabzenMarkerWrapper(forcedMarker, sceneMarker, statusMarker, tagMarker))
    }
  }

  override fun log() {
    if (condition && (enabled || forcedMarker != null)) {
      addMarkerIfNecessary()
      super.log()
    }
  }

  override fun log(message: String) {
    if (condition && (enabled || forcedMarker != null)) {
      addMarkerIfNecessary()
      super.log(message)
    }
  }

  override fun log(message: String, arg: Any) {
    if (condition && (enabled || forcedMarker != null)) {
      addMarkerIfNecessary()
      super.log(message, arg)
    }
  }

  override fun log(message: String, arg0: Any, arg1: Any) {
    if (condition && (enabled || forcedMarker != null)) {
      addMarkerIfNecessary()
      super.log(message, arg0, arg1)
    }
  }

  override fun log(message: String, vararg args: Any) {
    if (condition && (enabled || forcedMarker != null)) {
      addMarkerIfNecessary()
      super.log(message, *args)
    }
  }

  override fun log(messageSupplier: Supplier<String>) {
    if (condition && (enabled || forcedMarker != null)) {
      addMarkerIfNecessary()
      super.log(messageSupplier.get())
    }
  }

  // ===============================================================================================

  override fun setCause(t: Throwable?): LabzenLoggingEventBuilder {
    super.setCause(t)
    return this
  }

  override fun addMarker(marker: Marker?): LabzenLoggingEventBuilder {
    super.addMarker(marker)
    return this
  }

  override fun addArgument(p: Any?): LabzenLoggingEventBuilder {
    super.addArgument(p)
    return this
  }

  override fun addArgument(objectSupplier: Supplier<*>?): LabzenLoggingEventBuilder {
    super.addArgument(objectSupplier)
    return this
  }

  override fun addKeyValue(key: String?, value: Any?): LabzenLoggingEventBuilder {
    super.addKeyValue(key, value)
    return this
  }

  override fun addKeyValue(key: String?, value: Supplier<Any>?): LabzenLoggingEventBuilder {
    super.addKeyValue(key, value)
    return this
  }

  override fun setMessage(message: String?): LabzenLoggingEventBuilder {
    super.setMessage(message)
    return this
  }

  override fun setMessage(messageSupplier: Supplier<String>?): LabzenLoggingEventBuilder {
    super.setMessage(messageSupplier)
    return this
  }
}
