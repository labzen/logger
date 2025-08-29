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

  private final boolean enabled;
  private final LabzenLogger labzenLogger;
  private final LabzenLoggingEvent labzenLoggingEvent;

  private Set<String> inProfiles;
  private Set<String> outProfiles;
  private boolean conditional = true;
  private ForcedMarker forcedMarker;
  private SceneMarker sceneMarker;
  private StatusMarker statusMarker;
  private TagMarker tagMarker;

  public LabzenLoggingEventBuilder(LabzenLogger logger, Level level) {
    super(logger, level);
    this.enabled = switch (level) {
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
   * 强制打印日志，忽略日志级别；如果condition为false，也会打印，即不论其他任何条件，只要强制就会打印
   */
  public LabzenLoggingEventBuilder force() {
    this.forcedMarker = new ForcedMarker();
    return this;
  }

  /**
   * 日志输出条件，当条件不成立时，不打印日志
   */
  public LabzenLoggingEventBuilder conditional(boolean value) {
    this.conditional = value;
    return this;
  }

  /**
   * 日志输出条件，当条件不成立时，不打印日志
   */
  public LabzenLoggingEventBuilder conditional(Supplier<Boolean> supplier) {
    return conditional(supplier.get());
  }

  /**
   * 当前spring的profile，有任意一个是该方法指定的profile时，就输出日志；忽略大小写；比{@link #conditional(boolean)}有效级低，
   * 即 conditional=false 时，不会判断profile
   * <p>
   * 例如：spring的profile是A,B，方法指定的是B,C，则输出日志
   */
  public LabzenLoggingEventBuilder inProfile(String... profiles) {
    this.inProfiles = Set.of(profiles);
    return this;
  }

  /**
   * 当前spring的profile，所有的都不是该方法指定的profile时，才输出日志；忽略大小写；比{@link #conditional(boolean)}有效级低，
   * 即 conditional=false 时，不会判断profile
   * <p>
   * 例如：spring的profile是A,B，方法指定的是B,C，则不输出日志；方法指定的C,D，则输出日志
   */
  public LabzenLoggingEventBuilder outProfile(String... profiles) {
    this.outProfiles = Set.of(profiles);
    return this;
  }

  /**
   * 增加场景辅助标识
   */
  public LabzenLoggingEventBuilder scene(Scenes scene) {
    return scene(scene.name());
  }

  /**
   * 增加场景辅助标识
   */
  public LabzenLoggingEventBuilder scene(String scene) {
    this.sceneMarker = new SceneMarker(scene);
    return this;
  }

  /**
   * 增加状态辅助标识
   */
  public LabzenLoggingEventBuilder status(Status status) {
    this.statusMarker = new StatusMarker(status.getText());
    return this;
  }

  /**
   * 增加状态辅助标识
   */
  public LabzenLoggingEventBuilder status(String status) {
    this.statusMarker = new StatusMarker(status);
    return this;
  }

  /**
   * 对日志加标签
   */
  public LabzenLoggingEventBuilder tags(String... tags) {
    return tags(List.of(tags));
  }

  /**
   * 对日志加标签
   */
  public LabzenLoggingEventBuilder tags(List<String> tags) {
    this.tagMarker = new TagMarker(tags);
    return this;
  }

  /**
   * 对日志打印计数，可配合tag与scene做区分计数
   * <p>
   * **!! 暂未实现 !!**
   */
  public LabzenLoggingEventBuilder counting() {
    return this;
  }

  /**
   * 提供阶段性日志，整个阶段过程中，可统计执行时长等信息
   * <p>
   * **!! 暂未实现 !!**
   */
  public LabzenLoggingEventBuilder phaseStart() {
    return this;
  }

  /**
   * 提供阶段性日志，整个阶段过程中，可统计执行时长等信息
   * <p>
   * **!! 暂未实现 !!**
   */
  public LabzenLoggingEventBuilder phasePause() {
    return this;
  }

  /**
   * 提供阶段性日志，整个阶段过程中，可统计执行时长等信息
   * <p>
   * **!! 暂未实现 !!**
   */
  public LabzenLoggingEventBuilder phaseEnd() {
    return this;
  }

  /**
   * 打印JSON数据
   */
  public LabzenLoggingEventBuilder json(String text) {
    labzenLoggingEvent.setCodeType(CodeTypes.JSON);
    labzenLoggingEvent.setCodeText(text);
    return this;
  }

  /**
   * 打印XML数据
   */
  public LabzenLoggingEventBuilder xml(String text) {
    labzenLoggingEvent.setCodeType(CodeTypes.XML);
    labzenLoggingEvent.setCodeText(text);
    return this;
  }

  /**
   * 打印YAML数据
   */
  public LabzenLoggingEventBuilder yaml(String text) {
    labzenLoggingEvent.setCodeType(CodeTypes.YAML);
    labzenLoggingEvent.setCodeText(text);
    return this;
  }

  /**
   * 给当前logger的每条日志，从下一行开始加上前缀
   *
   * @param prefix 前缀字符
   */
  public LabzenLoggingEventBuilder startPrefix(String prefix) {
    return startPrefix(prefix, false);
  }

  /**
   * 给当前logger的每条日志，加上前缀
   *
   * @param prefix 前缀字符
   * @param now    true - 从当前日志开始，false - 从下一行开始
   */
  public LabzenLoggingEventBuilder startPrefix(String prefix, boolean now) {
    labzenLogger.startMessagePrefix(prefix, now);
    return this;
  }

  /**
   * 从下一行开始结束logger打印日志时加的前缀
   */
  public LabzenLoggingEventBuilder endPrefix() {
    return endPrefix(false);
  }

  /**
   * 结束logger打印日志时加的前缀
   *
   * @param now true - 从当前日志开始，false -
   */
  public LabzenLoggingEventBuilder endPrefix(boolean now) {
    labzenLogger.endMessagePrefix(now);
    return this;
  }

  // ===============================================================================================

  public void addMarkerIfNecessary() {
    if (forcedMarker != null || sceneMarker != null || statusMarker != null || tagMarker != null) {
      addMarker(new MarkerWrapper(forcedMarker, sceneMarker, statusMarker, tagMarker));
    }
  }

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

  @Override
  public void log() {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log();
    }
  }

  public void log(String message) {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log(message);
    }
  }

  public void log(String message, Object arg) {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log(message, arg);
    }
  }

  public void log(String message, Object arg0, Object arg1) {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log(message, arg0, arg1);
    }
  }

  public void log(String message, Object... args) {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log(message, args);
    }
  }

  public void log(Supplier<String> messageSupplier) {
    if (logEnabled()) {
      addMarkerIfNecessary();
      super.log(messageSupplier.get());
    }
  }
}
