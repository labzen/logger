package cn.labzen.logger.kernel;

import cn.labzen.logger.kernel.enums.CodeTypes;
import cn.labzen.logger.kernel.enums.Scenes;
import cn.labzen.logger.kernel.enums.Status;
import cn.labzen.logger.kernel.marker.*;
import cn.labzen.logger.spring.Profiles;
import org.slf4j.event.Level;
import org.slf4j.spi.DefaultLoggingEventBuilder;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * TODO 完善注释
 * 打印日志模板，等待日志的参数计算，有了返回值然后打印日志
 * <p>
 * 日志模板中placeholder支持样式：
 * <code>
 * <pre>
 *  1. {} - 默认占位，如果有多个，则按照给定参数出现的顺序，依次替换模板中的占位；
 *  2. {0} - 指参占位，顺序占位，在存在有多个参数List&lt;Object&gt;情况下，从0开始指定占位在参数集合中的位置；
 *  3. {param_name} - 指参占位，参数名占位，给定的参数需要是Map<String, Object>，对应参数名替换模板中的占位，param_name必须以字母开头；
 *  4. &#123;@number_0.00} - 格式化占位，数字格式化占位，按给定的pattern参数（具体格式参考DecimalFormat类），格式化输出。0代表一个数字，#代表一个数字但不补零；
 *     例如：double x = 16.7;
 *          &#123;@number_0} // 打印 "16";
 *          &#123;@number_0.0} // 打印 "16.7";
 *          &#123;@number_0.00} // 打印 "16.70";
 *          &#123;@number_000.##} // 打印 "016.7";
 *  5. &#123;@date_yyyy-MM-dd} - 格式化占位，日期格式化占位，按给定的Date等日期时间类型参数，格式化输出。支持Date, LocalDate, LocalTime, LocalDateTime，毫秒数等类型；
 *     例如：&#123;@date_yyyy-MM} // 打印 "2021-01"
 *  6. &#123;@wrap_[]} - 函数式占位，将参数使用给定的字符包裹输出。这里wrap是函数，后边带单引号，内部作为包裹参数，包裹参数为2位固定长度的字符串；
 *     例如：String x = "123";
 *          &#123;@wrap_()} // 打印 "(123)";
 *          &#123;@wrap_\{\}} // 打印 "{123}"; （大括号，单引号，双引号需要转义）
 *  7. &#123;@whether_yes,no} - 函数式占位，仅适用于boolean类型的参数。这里whether是函数，后边带单引号，内部两个参数，用逗号分隔不需要空格，当参数为true时，输出第一个参数，否则输出第二个参数；
 *     例如：boolean x = false;
 *          &#123;@whether yes, no} // 打印 "no"
 *  8. &#123;@width_} - 位宽指定，可指定两个数字值，使用逗号分隔，不需要空格，第一个数字代表最小位宽，即保证日志打印的参数至少占用多少个字符，如果实际占用字符超出这个设置，等效于忽略该数值。第二个数字代表最大位宽（可忽略），即保证日志打印的参数最多占用多少个字符，如果超出，则截断。
 *     例如：String x = "1234567890";
 *          &#123;@width_5} // 打印 “1234567890”;
 *          &#123;@width_12} // 打印 "1234567890  ";
 *          &#123;@width_0,5} // 打印 "12345"
 * </pre>
 * </code>
 * 按上述规则，占位符顺序分为1默认占位，2指参占位，3格式化占位，4函数占位，5位宽
 * <p>
 * 除默认占位，其他四种占位，可组合使用，但需要按照顺序出现，相同的占位类型，不需要关心出现的顺序，但会按照顺序去处理。例如：
 * <ul>
 * <li>
 *   {price@number_0.0@wrap_||@width_20} 表示取key为price的number数值，格式化为十进制小数点后保留1位的字符串，然后计算16位的hash值大写，并用两个|包裹起来，输出时保证所占宽度为20位。示例结果为："[EC9BC01D9A00E0A8]  "
 * </li>
 * <li>
 *   {3@whether_是,否@wrap_<>} 表示取下标为3的boolean值，如果为true，输出是。示例结果为："<是>"
 * </li>
 * </ul>
 * <p>
 * 参数值，可以为：
 * <ol>
 * <li>Map&lt;String, Object&gt;（日志模板的placeholder需要指定对应的参数名）</li>
 * <li>List&lt;Object&gt;（按顺序对应日志模板中的placeholder）</li>
 * <li>其他Object类型（只能对应日志模板中的一个参数placeholder）</li>
 * </ol>
 */
public class LabzenLoggingEventBuilder extends DefaultLoggingEventBuilder {

  /** 日志级别是否启用 */
  private final boolean enabled;

  /** 关联的LabzenLogger实例 */
  private final LabzenLogger labzenLogger;

  /** 关联的日志事件对象 */
  private final LabzenLoggingEvent labzenLoggingEvent;

  /** 包含的Profile白名单 */
  private Set<String> inProfiles;

  /** 排除的Profile黑名单 */
  private Set<String> outProfiles;

  /** 条件标志，false时不打印日志（除非force） */
  private boolean conditional = true;

  /** 强制打印标记 */
  private ForcedMarker forcedMarker;

  /** 场景标记 */
  private SceneMarker sceneMarker;

  /** 状态标记 */
  private StatusMarker statusMarker;

  /** 标签标记 */
  private TagMarker tagMarker;

  /**
   * 构造方法
   *
   * @param logger 日志器实例
   * @param level  日志级别
   */
  public LabzenLoggingEventBuilder(LabzenLogger logger, Level level) {
    super(logger, level);

    this.enabled = switch (level) {
      case null -> false;
      case TRACE -> logger.isTraceEnabled();
      case DEBUG -> logger.isDebugEnabled();
      case INFO -> logger.isInfoEnabled();
      case WARN -> logger.isWarnEnabled();
      case ERROR -> logger.isErrorEnabled();
    };

    labzenLogger = logger;
    labzenLoggingEvent = new LabzenLoggingEvent(level, logger);
    loggingEvent = labzenLoggingEvent;
  }

  /**
   * 强制打印日志，忽略日志级别限制
   *
   * <p>即使日志级别被禁用（如生产环境DEBUG被禁用），只要force就会打印
   *
   * @return 当前Builder，支持链式调用
   */
  public LabzenLoggingEventBuilder force() {
    this.forcedMarker = new ForcedMarker();
    return this;
  }

  /**
   * 设置日志输出条件
   *
   * @param value true-允许打印，false-禁止打印
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder conditional(boolean value) {
    this.conditional = value;
    return this;
  }

  /**
   * 设置日志输出条件（延迟计算）
   *
   * @param supplier 条件提供者，用于延迟执行避免不必要的计算
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder conditional(Supplier<Boolean> supplier) {
    return conditional(supplier.get());
  }

  /**
   * 设置白名单Profile
   *
   * <p>当Spring的active profiles中任意一个匹配时输出日志，大小写不敏感
   *
   * @param profiles Profile名称数组
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder inProfile(String... profiles) {
    this.inProfiles = Set.of(profiles);
    return this;
  }

  /**
   * 设置黑名单Profile
   *
   * <p>当Spring的active profiles中所有都不匹配时输出日志，大小写不敏感
   *
   * @param profiles 排除的Profile名称数组
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder outProfile(String... profiles) {
    this.outProfiles = Set.of(profiles);
    return this;
  }

  /**
   * 添加场景标识（枚举方式）
   *
   * @param scene 场景枚举
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder scene(Scenes scene) {
    return scene(scene.name());
  }

  /**
   * 添加场景标识
   *
   * @param scene 场景名称
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder scene(String scene) {
    this.sceneMarker = new SceneMarker(scene);
    return this;
  }

  /**
   * 添加状态标识（枚举方式）
   *
   * @param status 状态枚举
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder status(Status status) {
    this.statusMarker = new StatusMarker(status.getText());
    return this;
  }

  /**
   * 添加状态标识
   *
   * @param status 状态文本
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder status(String status) {
    this.statusMarker = new StatusMarker(status);
    return this;
  }

  /**
   * 添加标签（可变参数方式）
   *
   * @param tags 标签数组
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder tags(String... tags) {
    return tags(List.of(tags));
  }

  /**
   * 添加标签
   *
   * @param tags 标签列表
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder tags(List<String> tags) {
    this.tagMarker = new TagMarker(tags);
    return this;
  }

  /**
   * 对日志打印计数，可配合tag与scene做区分计数
   *
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder counting() {
    return this;
  }

  /**
   * 标记阶段性日志开始
   *
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder phaseStart() {
    return this;
  }

  /**
   * 标记阶段性日志暂停
   *
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder phasePause() {
    return this;
  }

  /**
   * 标记阶段性日志结束
   *
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder phaseEnd() {
    return this;
  }

  /**
   * 设置打印内容为JSON格式
   *
   * <p>会在输出时添加JSON边框装饰
   *
   * @param text JSON文本
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder json(String text) {
    labzenLoggingEvent.setCodeType(CodeTypes.JSON);
    labzenLoggingEvent.setCodeText(text);
    return this;
  }

  /**
   * 设置打印内容为XML格式
   *
   * <p>会在输出时添加XML边框装饰
   *
   * @param text XML文本
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder xml(String text) {
    labzenLoggingEvent.setCodeType(CodeTypes.XML);
    labzenLoggingEvent.setCodeText(text);
    return this;
  }

  /**
   * 设置打印内容为YAML格式
   *
   * <p>会在输出时添加YAML边框装饰
   *
   * @param text YAML文本
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder yaml(String text) {
    labzenLoggingEvent.setCodeType(CodeTypes.YAML);
    labzenLoggingEvent.setCodeText(text);
    return this;
  }

  /**
   * 开始消息前缀（从下一行生效）
   *
   * @param prefix 前缀字符
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder startPrefix(String prefix) {
    return startPrefix(prefix, false);
  }

  /**
   * 开始消息前缀
   *
   * @param prefix 前缀字符
   * @param now    true-立即生效（当前日志开始），false-延迟生效（下一行开始）
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder startPrefix(String prefix, boolean now) {
    labzenLogger.startMessagePrefix(prefix, now);
    return this;
  }

  /**
   * 结束消息前缀（从下一行生效）
   *
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder endPrefix() {
    return endPrefix(false);
  }

  /**
   * 结束消息前缀
   *
   * @param now true-立即停止，false-延迟停止（从下一行开始）
   * @return 当前Builder
   */
  public LabzenLoggingEventBuilder endPrefix(boolean now) {
    labzenLogger.endMessagePrefix(now);
    return this;
  }

  // ===============================================================================================

  /**
   * 必要时添加Marker
   *
   * <p>当存在forced/scene/status/tag任一Marker时，包装并添加到事件
   */
  public void addMarkerIfNecessary() {
    if (forcedMarker != null || sceneMarker != null || statusMarker != null || tagMarker != null) {
      addMarker(new MarkerWrapper(forcedMarker, sceneMarker, statusMarker, tagMarker));
    }
  }

  /**
   * 判断日志是否应该输出
   *
   * <p>判断优先级：
   * <ol>
   *   <li>forcedMarker存在 → 直接输出</li>
   *   <li>日志级别未启用 → 不输出</li>
   *   <li>conditional=false → 不输出</li>
   *   <li>inProfiles匹配 → 输出</li>
   *   <li>outProfiles不匹配 → 输出</li>
   *   <li>其他 → 不输出</li>
   * </ol>
   *
   * @return true表示应该输出日志
   */
  private boolean logEnabled() {
    if (forcedMarker != null) {
      // 强制输出，不需要考虑其他条件
      return true;
    }

    if (!enabled) {
      // 日志级别未开启，则不再考虑条件和profile
      return false;
    }

    if (!conditional) {
      return false;
    }

    if (inProfiles != null) {
      // 当前Spring的Profile有任意一个在inProfiles中出现，则可输出日志
      return Profiles.currentProfiles().stream().anyMatch(pro -> inProfiles.contains(pro));
    } else if (outProfiles != null) {
      // 当前Spring的Profile所有都不在notInProfiles中出现，则可输出日志
      return Profiles.currentProfiles().stream().noneMatch(pro -> outProfiles.contains(pro));
    } else {
      return true;
    }
  }

  /**
   * 输出日志（无消息）
   */
  @Override
  public void log() {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log();
    }
  }

  /**
   * 输出日志
   *
   * @param message 消息文本
   */
  public void log(String message) {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log(message);
    }
  }

  /**
   * 输出日志（带1个参数）
   *
   * @param message 消息模板
   * @param arg     参数
   */
  public void log(String message, Object arg) {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log(message, arg);
    }
  }

  /**
   * 输出日志（带2个参数）
   *
   * @param message 消息模板
   * @param arg0    参数1
   * @param arg1    参数2
   */
  public void log(String message, Object arg0, Object arg1) {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log(message, arg0, arg1);
    }
  }

  /**
   * 输出日志（带可变参数）
   *
   * @param message 消息模板
   * @param args    参数数组
   */
  public void log(String message, Object... args) {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log(message, args);
    }
  }

  /**
   * 输出日志（消息延迟计算）
   *
   * @param messageSupplier 消息提供者
   */
  public void log(Supplier<String> messageSupplier) {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log(messageSupplier.get());
    }
  }
}
