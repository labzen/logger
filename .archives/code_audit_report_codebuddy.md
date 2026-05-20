The user wants me to do a code review of the logger project. They've attached the folder `c:\Working\labzen\logger` and have a specific review format to follow. I already have the detailed review from the previous conversation, but now I need to format it according to the specified template.

Let me re-read the currently opened file and then produce the review report in the specified format. The instructions say I should review files in the `<attached_files>` tag, which is the entire logger folder.

I already have comprehensive findings from the previous deep review. Let me now format them according to the new template, focusing on the most critical and actionable issues. Let me also verify a few of the key findings by reading the actual files to make sure I have accurate line numbers and code excerpts.

Let me read the key files to verify the exact code and line numbers for the issues I'll report.
Now I have the actual file contents. Let me review them and produce the report. I notice some differences from the previous analysis:

1. `LabzenReload4jLoggingSystem.setLogLevel()` - Actually, looking at the current code at lines 116-125, the logic seems CORRECT now: `if (StringUtils.hasText(loggerName))` → gets the named logger; `else` → gets the root logger. The previous analysis was wrong. Wait, let me re-read...

```java
private void setLogLevel(String loggerName, Level level) {
    LoggerRepository loggerRepository = LogManager.getLoggerRepository();
    Logger logger;
    if (StringUtils.hasText(loggerName)) {
      logger = loggerRepository.getLogger(loggerName);
    } else {
      logger = loggerRepository.getRootLogger();
    }
    logger.setLevel(level);
}
```

Actually, the current code IS correct! `StringUtils.hasText(loggerName)` returns true when loggerName has text → use that name to get the logger. When false (empty/null) → get root logger. This is the correct logic. The previous analysis was wrong about this one.

Wait, but looking at the original deep review, it said:
```java
if (StringUtils.hasText(loggerName)) {    // BUG: hasText 为 true 时应取指定 logger
    logger = loggerRepository.getRootLogger();  // 却取了 root logger
} else {
    logger = loggerRepository.getLogger(loggerName);  // hasText 为 false 时 loggerName 为空/blank
}
```

But the actual code I just read says:
```java
if (StringUtils.hasText(loggerName)) {
    logger = loggerRepository.getLogger(loggerName);
} else {
    logger = loggerRepository.getRootLogger();
}
```

The code is correct! The previous deep review was likely based on an older version or an incorrect reading. I need to be accurate here.

2. `AbstractLimitedMarker.getName()` - Actually, looking at the current code at line 31-33, it returns `""` (empty string), NOT throwing an exception. The methods that DO throw exceptions are `add()`, `remove()`, `iterator()`, `contains(Marker)`, and `contains(String)`. So this is LESS severe than previously thought - `getName()` is safe.

3. `LabzenLogger.startMessagePrefix()` and `endMessagePrefix()` - Both are `synchronized` (lines 75 and 88), so the race condition issue from the previous review is actually already fixed!

4. `FormatterOfNumberTile.convert()` - Looking at lines 61-69, the null check IS present (lines 65-67), but it comes AFTER the `instanceof Number` check. So if value is null and not a Number, it returns "". This is fine. But the `DecimalFormat` thread safety issue still exists.

5. `FunctionalWidthControlTile.convert()` - Looking at line 68, the null check IS present: `String string = value == null ? "" : value.toString();`. So the NPE issue is already fixed.

6. `FunctionalWrapTile.convert()` - Looking at lines 51-55, the null check IS present: if value is null, returns "". So the NPE issue is already fixed.

7. `LabzenLogbackLoggerContext` - Looking at the current code, `INSTANCE` is now `volatile` (line 14), and `LOGGER_CACHE` is used with `computeIfAbsent` (line 26). The logger caching is already implemented! Also, the instance assignment is now after the initialization.

8. `FormatterOfDateTile.convert()` - Looking at line 77, it uses `((Number) value).longValue()` which is correct and won't throw ClassCastException for Integer values.

9. `LabzenNOPLoggingEventBuilder` - Looking at lines 42-43, it passes `LOGGER` (a LabzenLogger instance) and `Level.TRACE`, not null,null. But there's a circular initialization issue: `SINGLETON` is created at class load time, which also triggers `LOGGER = Loggers.getLogger(LabzenNOPLoggingEventBuilder.class)` - this could be problematic during initialization.

Let me now focus on the actual issues I can confirm from the code I've read:

**Confirmed Issues:**

1. **DecimalFormat thread safety** - `FormatterOfNumberTile` caches `DecimalFormat` instances in `ConcurrentHashMap`, but `DecimalFormat` is not thread-safe. Multiple threads calling `formatter.format(value)` concurrently can produce incorrect results.

2. **MetaPrinter sensitive info leakage** - Line 110 prints JVM input arguments without sanitization, potentially exposing passwords.

3. **Loggers.getLogger() unchecked cast** - Lines 54-55, 66-67 directly cast without checking the type.

4. **LabzenLogger Supplier overloads don't check log level** - Lines 253-254, 364-365, etc. always call `supplier.get()` even when the level is disabled.

5. **LabzenLoggingEventBuilder.conditional(Supplier) immediately evaluates** - Line 152 calls `supplier.get()` immediately, defeating the lazy evaluation purpose.

6. **AbstractLimitedMarker methods throw IllegalStateException** - `add()`, `remove()`, `iterator()`, `contains(Marker)`, `contains(String)` all throw. If Logback internal code calls these, it will crash.

7. **LabzenNOPLoggingEventBuilder circular initialization** - `SINGLETON` uses `LOGGER` which is initialized at class load time, but `LOGGER` needs the full logging system to be initialized. This could cause initialization order issues.

8. **LabzenLogger.mergeMarkersAndKeyValuePairs - event.getArgumentArray() could be null** - Line 207, `MessageFormatter.arrayFormat(message, event.getArgumentArray())` - getArgumentArray() could return null.

Let me also read more files to check for other issues.

Actually, let me be more thorough and check additional files.
Now I have a much more accurate picture. Let me compare my initial analysis with the actual code and identify the real issues:

1. **`LoggerImplementor`** - `volatile` is already used (line 26, 29). ✅ Already fixed.

2. **`LabzenLogger.startMessagePrefix()`/`endMessagePrefix()`** - Already `synchronized` (lines 75, 88). ✅ Already fixed.

3. **`LabzenLogbackLoggerContext`** - Already uses `volatile` for INSTANCE (line 14) and has `LOGGER_CACHE` with `computeIfAbsent` (lines 15, 26). ✅ Already fixed.

4. **`FunctionalWidthControlTile.convert()`** - Already has null check (line 68). ✅ Already fixed.

5. **`FunctionalWrapTile.convert()`** - Already has null check (lines 52-54). ✅ Already fixed.

6. **`FormatterOfDateTile.convert()`** - Uses `((Number) value).longValue()` (line 77), which is correct. ✅ Already fixed.

7. **`IdentifiableLoggerConverter.convert()`** - Now calls `logText(event)` (line 78). ✅ Already fixed.

8. **`AbstractLimitedMarker.getName()`** - Returns `""` (line 32), doesn't throw. But `add()`, `remove()`, `iterator()`, `contains()` still throw.

9. **`LabzenReload4jLoggingSystem.setLogLevel()`** - The logic IS correct (lines 119-122). ✅ Already correct.

10. **`FormatterOfNumberTile.convert()`** - Has null check (lines 65-67), but the `DecimalFormat` thread safety issue still exists (line 63).

11. **`MarkerWrapper.tailed`** - `volatile` is already used (line 33). But the check-then-act in `getTailed()` still has a minor race.

So the real confirmed issues are:

**High:**
1. `FormatterOfNumberTile` - `DecimalFormat` is not thread-safe, shared across threads via `FORMATTER_CACHE`
2. `MetaPrinter` - JVM startup arguments may contain sensitive info (passwords, keys)
3. `Loggers.getLogger()` - Unchecked cast to `LabzenLogger`
4. `AbstractLimitedMarker` - Several methods throw `IllegalStateException`, violating SLF4J Marker contract

**Medium:**
5. `LabzenLogger` Supplier overloads don't check log level before evaluating
6. `LabzenLoggingEventBuilder.conditional(Supplier)` immediately evaluates, defeating lazy purpose
7. `Profiles.currentProfiles` - `ArrayList` with non-atomic `clear()` + `addAll()`
8. `LabzenLogger.mergeMarkersAndKeyValuePairs` - `event.getArgumentArray()` could be null
9. `LabzenLogbackServiceProvider.enhancePatternLayout()` - Pollutes global static map, potential conflicts
10. `LabzenLoggingEvent.getMessage()` double formatting issue
11. `LabzenNOPLoggingEventBuilder` circular initialization - `LOGGER` field initialized at class load time before logging system is ready

Let me now produce the final review report.
### 整体评估

⚠️ **Risky**

项目在 Fluent API 设计、Tile 占位符系统等方面展现了良好的工程能力，但存在 `DecimalFormat` 线程安全、敏感信息明文输出、SLF4J Marker 契约违反等较高风险问题，在并发场景和生产环境下可能导致数据错误、凭据泄露或系统崩溃。

---

### Issue 清单

#### 1. 并发安全 — DecimalFormat 非线程安全 🔴

**Location:** `src/main/java/cn/labzen/logger/kernel/tile/FormatterOfNumberTile.java#L30-L63`

**Analysis:** `DecimalFormat` 不是线程安全的，但通过 `FORMATTER_CACHE`（`ConcurrentHashMap`）缓存后，所有使用相同 pattern 的线程会共享同一个 `DecimalFormat` 实例。多线程并发调用 `formatter.format(value)` 时，`DecimalFormat` 内部的 `DigitList` 和 `FieldPosition` 状态可能被并发修改，导致格式化结果错误、数字精度丢失或抛出 `ArrayIndexOutOfBoundsException`。

**Fix Recommendation:**

```java
// FILEPATH: src/main/java/cn/labzen/logger/kernel/tile/FormatterOfNumberTile.java

// ------ ORIGINAL CODE ------
public class FormatterOfNumberTile extends AbstractTile<String> {

  private static final Map<String, DecimalFormat> FORMATTER_CACHE = new ConcurrentHashMap<>();

  private final NumberFormat formatter;

  public FormatterOfNumberTile(String pattern) {
    formatter = FORMATTER_CACHE.computeIfAbsent(pattern, DecimalFormat::new);
  }

  @Override
  public String convert(Object value) {
    if (value instanceof Number) {
      return formatter.format(value);
    }
    if (value == null) {
      return "";
    }
    return value.toString();
  }
// --------------------------
// ------ NEW CODE ----------
public class FormatterOfNumberTile extends AbstractTile<String> {

  private static final Map<String, ThreadLocal<NumberFormat>> FORMATTER_CACHE = new ConcurrentHashMap<>();

  private final String pattern;

  public FormatterOfNumberTile(String pattern) {
    this.pattern = pattern;
    FORMATTER_CACHE.computeIfAbsent(pattern, p -> ThreadLocal.withInitial(() -> new DecimalFormat(p)));
  }

  @Override
  public String convert(Object value) {
    if (value instanceof Number) {
      return FORMATTER_CACHE.get(pattern).get().format(value);
    }
    if (value == null) {
      return "";
    }
    return value.toString();
  }
// --------------------------
```

---

#### 2. 安全漏洞 — JVM 启动参数可能泄露敏感信息 🔴

**Location:** `src/main/java/cn/labzen/logger/print/MetaPrinter.java#L109-L110`

**Analysis:** `runtimeMXBean.getInputArguments()` 包含所有 JVM 启动参数，可能含 `-Ddb.password=xxx`、`-Dspring.datasource.password=xxx`、`-Djavax.net.ssl.keyStorePassword=xxx` 等敏感凭据。这些信息被原样写入日志文件，任何能访问日志文件的人都能获取到数据库密码、API Key 等。违反 OWASP A02:2021 (Cryptographic Failures) 和 PCI-DSS 等安全合规要求。

**Fix Recommendation:**

```java
// FILEPATH: src/main/java/cn/labzen/logger/print/MetaPrinter.java

// ------ ORIGINAL CODE ------
    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    logger.atInfo().scene(LABZEN_TEXT).log("JVM启动输入参数：{}", String.join(" ", runtimeMXBean.getInputArguments()));
// --------------------------
// ------ NEW CODE ----------
    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    String sanitizedArgs = runtimeMXBean.getInputArguments().stream()
        .map(arg -> arg.replaceAll("(?i)(password|passwd|secret|key|token|credential)\\s*=\\s*\\S+", "$1=****"))
        .collect(Collectors.joining(" "));
    logger.atInfo().scene(LABZEN_TEXT).log("JVM启动输入参数：{}", sanitizedArgs);
// --------------------------
```

---

#### 3. API 契约违反 — AbstractLimitedMarker 多个方法抛出异常 🔴

**Location:** `src/main/java/cn/labzen/logger/kernel/marker/AbstractLimitedMarker.java#L40-L103`

**Analysis:** SLF4J 的 `Marker` 接口是核心契约，Logback 的 `TurboFilter.decide()`、`PatternLayout` 等组件会调用 `Marker.contains()`、`Marker.iterator()` 等方法。当前实现中 `add()`、`remove()`、`iterator()`、`contains(Marker)`、`contains(String)` 全部抛出 `IllegalStateException`，一旦 Logback 内部代码调用这些方法，将导致未捕获异常、日志输出中断甚至应用崩溃。`MarkerWrapper` 被传入 `ForcedFilter` 和 `addMarker()` 调用链中，触发风险较高。

**Fix Recommendation:**

```java
// FILEPATH: src/main/java/cn/labzen/logger/kernel/marker/AbstractLimitedMarker.java

// ------ ORIGINAL CODE ------
  @Override
  public void add(Marker reference) {
    throw new IllegalStateException("unnecessary method");
  }

  @Override
  public boolean remove(Marker reference) {
    throw new IllegalStateException("unnecessary method");
  }

  @Override
  public boolean hasChildren() {
    return false;
  }

  @Override
  public boolean hasReferences() {
    return false;
  }

  @Override
  public Iterator<Marker> iterator() {
    throw new IllegalStateException("unnecessary method");
  }

  @Override
  public boolean contains(Marker other) {
    throw new IllegalStateException("unnecessary method");
  }

  @Override
  public boolean contains(String name) {
    throw new IllegalStateException("unnecessary method");
  }
// --------------------------
// ------ NEW CODE ----------
  @Override
  public void add(Marker reference) {
    // no-op: 不支持子Marker层级结构
  }

  @Override
  public boolean remove(Marker reference) {
    return false;
  }

  @Override
  public boolean hasChildren() {
    return false;
  }

  @Override
  public boolean hasReferences() {
    return false;
  }

  @Override
  public Iterator<Marker> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public boolean contains(Marker other) {
    return false;
  }

  @Override
  public boolean contains(String name) {
    return false;
  }
// --------------------------
```

---

#### 4. 类型安全 — Loggers.getLogger() 无检查强转 🟡

**Location:** `src/main/java/cn/labzen/logger/Loggers.java#L54-L68`

**Analysis:** `Loggers.getLogger()` 直接将 `LoggerFactory.getLogger()` 的返回值强转为 `LabzenLogger`。如果 `enhance()` 未在首次调用前执行，或 SLF4J 使用了其他 ServiceProvider（如原生 LogbackServiceProvider），返回的将不是 `LabzenLogger` 实例，此时强转抛出未捕获的 `ClassCastException`，导致应用启动即崩溃。

**Fix Recommendation:**

```java
// FILEPATH: src/main/java/cn/labzen/logger/Loggers.java

// ------ ORIGINAL CODE ------
  public static LabzenLogger getLogger(String name) {
    return (LabzenLogger) LoggerFactory.getLogger(name);
  }

  public static LabzenLogger getLogger(Class<?> clazz) {
    return (LabzenLogger) LoggerFactory.getLogger(clazz);
  }
// --------------------------
// ------ NEW CODE ----------
  public static LabzenLogger getLogger(String name) {
    Logger logger = LoggerFactory.getLogger(name);
    if (logger instanceof LabzenLogger labzenLogger) {
      return labzenLogger;
    }
    return new LabzenLogger(logger);
  }

  public static LabzenLogger getLogger(Class<?> clazz) {
    Logger logger = LoggerFactory.getLogger(clazz);
    if (logger instanceof LabzenLogger labzenLogger) {
      return labzenLogger;
    }
    return new LabzenLogger(logger);
  }
// --------------------------
```

---

#### 5. 并发安全 — Profiles.setCurrentProfiles() 非原子操作 🟡

**Location:** `src/main/java/cn/labzen/logger/spring/Profiles.java#L17-L29`

**Analysis:** `currentProfiles` 使用 `ArrayList`，`setCurrentProfiles()` 中 `clear()` + `addAll()` 不是原子操作。在 `clear()` 执行后、`addAll()` 执行前，`currentProfiles()` 通过 `List.copyOf(currentProfiles)` 会捕获到一个空列表。此时 `LabzenLoggingEventBuilder.logEnabled()` 中的 profile 匹配判断将基于空列表执行，导致本应输出的日志被丢弃，或本应屏蔽的日志被输出。

**Fix Recommendation:**

```java
// FILEPATH: src/main/java/cn/labzen/logger/spring/Profiles.java

// ------ ORIGINAL CODE ------
  private static final List<String> currentProfiles = new ArrayList<>();

  public static List<String> currentProfiles() {
    return List.copyOf(currentProfiles);
  }

  static void setCurrentProfiles(List<String> profiles) {
    currentProfiles.clear();
    currentProfiles.addAll(profiles);
  }
// --------------------------
// ------ NEW CODE ----------
  private static volatile List<String> currentProfiles = List.of();

  public static List<String> currentProfiles() {
    return currentProfiles;
  }

  static void setCurrentProfiles(List<String> profiles) {
    currentProfiles = List.copyOf(profiles);
  }
// --------------------------
```

---

#### 6. 性能缺陷 — Supplier 重载未检查日志级别 🟡

**Location:** `src/main/java/cn/labzen/logger/kernel/LabzenLogger.java#L253-L254` (及 L364-L365, L475-L476, L586-L587, L697-L698)

**Analysis:** 所有 5 个日志级别的 `Supplier<String>` 重载方法，均无条件调用 `supplier.get()`，即使对应级别已被禁用。`Supplier` 的设计初衷是延迟计算（仅在日志需要输出时才执行），但当前实现完全丧失了此优势。在生产环境中 `logger.debug(() -> expensiveOperation())` 中的 `expensiveOperation()` 总会被执行，可能引入严重性能问题。

**Fix Recommendation:**

```java
// FILEPATH: src/main/java/cn/labzen/logger/kernel/LabzenLogger.java

// ------ ORIGINAL CODE ------
  public void trace(Supplier<String> supplier) {
    principal.trace(supplier.get());
  }
// --------------------------
// ------ NEW CODE ----------
  public void trace(Supplier<String> supplier) {
    if (principal.isTraceEnabled()) {
      principal.trace(supplier.get());
    }
  }
// --------------------------
```

> 同理需修复 `debug(Supplier)`、`info(Supplier)`、`warn(Supplier)`、`error(Supplier)` 及所有 `Throwable + Supplier` 重载。

---

#### 7. 性能缺陷 — conditional(Supplier) 立即计算，违背延迟语义 🟡

**Location:** `src/main/java/cn/labzen/logger/kernel/LabzenLoggingEventBuilder.java#L150-L153`

**Analysis:** `conditional(Supplier<Boolean>)` 方法的 Javadoc 明确说"用于延迟执行避免不必要的计算"，但实现中 `supplier.get()` 被立即调用。调用者使用 `conditional(() -> checkFeature())` 期望只在 `log()` 时才评估条件，但实际上在构建阶段就执行了，失去了延迟计算的性能优势，且在高频日志场景下可能引入不必要的开销。

**Fix Recommendation:**

```java
// FILEPATH: src/main/java/cn/labzen/logger/kernel/LabzenLoggingEventBuilder.java

// ------ ORIGINAL CODE ------
  /** 条件标志，false时不打印日志（除非force） */
  private boolean conditional = true;

  public LabzenLoggingEventBuilder conditional(boolean value) {
    this.conditional = value;
    return this;
  }

  public LabzenLoggingEventBuilder conditional(Supplier<Boolean> supplier) {
    // 立即计算！失去延迟优势；存储 Supplier 对象，在真正打印日志时再计算
    return conditional(supplier.get());
  }
// --------------------------
// ------ NEW CODE ----------
  /** 条件标志，false时不打印日志（除非force） */
  private boolean conditional = true;

  /** 延迟计算的条件提供者 */
  private Supplier<Boolean> conditionalSupplier;

  public LabzenLoggingEventBuilder conditional(boolean value) {
    this.conditional = value;
    this.conditionalSupplier = null;
    return this;
  }

  public LabzenLoggingEventBuilder conditional(Supplier<Boolean> supplier) {
    this.conditionalSupplier = supplier;
    return this;
  }
// --------------------------
```

同时在 `logEnabled()` 方法中增加延迟计算的调用：

```java
// FILEPATH: src/main/java/cn/labzen/logger/kernel/LabzenLoggingEventBuilder.java

// ------ ORIGINAL CODE ------
    if (!conditional) {
      return false;
    }
// --------------------------
// ------ NEW CODE ----------
    if (conditionalSupplier != null) {
      if (!conditionalSupplier.get()) {
        return false;
      }
    } else if (!conditional) {
      return false;
    }
// --------------------------
```

---

#### 8. 空指针风险 — mergeMarkersAndKeyValuePairs 未处理 null 参数数组 🟡

**Location:** `src/main/java/cn/labzen/logger/kernel/LabzenLogger.java#L207`

**Analysis:** `event.getArgumentArray()` 在日志无参数时可能返回 `null`。`MessageFormatter.arrayFormat()` 在某些 SLF4J 实现中对 `null` 参数数组会抛出 `NullPointerException`，导致整条日志输出失败。

**Fix Recommendation:**

```java
// FILEPATH: src/main/java/cn/labzen/logger/kernel/LabzenLogger.java

// ------ ORIGINAL CODE ------
    sb.append(MessageFormatter.arrayFormat(message, event.getArgumentArray()).getMessage());
// --------------------------
// ------ NEW CODE ----------
    Object[] args = event.getArgumentArray();
    sb.append(MessageFormatter.arrayFormat(message, args != null ? args : new Object[0]).getMessage());
// --------------------------
```

---

#### 9. 设计缺陷 — 全局静态 Map 污染 🟡

**Location:** `src/main/java/cn/labzen/logger/logback/LabzenLogbackServiceProvider.java#L30-L44`

**Analysis:** `PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP` 是 Logback 的全局静态 Map。直接向其注入自定义转换器（如 `thrown`、`brief`、`bl` 等常见名称）可能覆盖 Logback 内置或用户已注册的同名转换器，在共享 JVM 的多应用环境（如 Servlet 容器）中还会影响其他应用。注释中也标注了 `newEx`/`newException` "可能有问题，还需要再验证"。

**Fix Recommendation:**

```java
// FILEPATH: src/main/java/cn/labzen/logger/logback/LabzenLogbackServiceProvider.java

// ------ ORIGINAL CODE ------
  private void enhancePatternLayout() {
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("showy", HighlighterConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("highlighter", HighlighterConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("briefLogger", IdentifiableLoggerConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("brief", IdentifiableLoggerConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("bl", IdentifiableLoggerConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("thrown", IndentedThrowableProxyConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("newEx", IndentedThrowableProxyConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("newException", IndentedThrowableProxyConverter::new);
  }
// --------------------------
// ------ NEW CODE ----------
  private void enhancePatternLayout() {
    // 使用 labzen 前缀避免与 Logback 内置或用户自定义转换器冲突
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("showy", HighlighterConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("labzenHighlighter", HighlighterConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("briefLogger", IdentifiableLoggerConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("labzenBrief", IdentifiableLoggerConverter::new);
    PatternLayout.DEFAULT_CONVERTER_SUPPLIER_MAP.put("labzenThrown", IndentedThrowableProxyConverter::new);
  }
// --------------------------
```

---

#### 10. 初始化风险 — LabzenNOPLoggingEventBuilder 类加载循环依赖 🟡

**Location:** `src/main/java/cn/labzen/logger/kernel/LabzenNOPLoggingEventBuilder.java#L34-L43`

**Analysis:** `SINGLETON` 在类加载时初始化，触发了 `LOGGER = Loggers.getLogger(LabzenNOPLoggingEventBuilder.class)` 的执行。此时 `LabzenLoggerServiceProvider` 可能尚未完成初始化，导致 `LoggerFactory.getLogger()` 返回非 `LabzenLogger` 类型的实例，后续强转失败或行为异常。此外 `SINGLETON` 传入 `LOGGER` 和 `Level.TRACE` 调用 `super(LOGGER, Level.TRACE)`，但 `LOGGER` 本身的初始化又依赖整个日志框架的初始化完成，形成了循环依赖。

**Fix Recommendation:**

```java
// FILEPATH: src/main/java/cn/labzen/logger/kernel/LabzenNOPLoggingEventBuilder.java

// ------ ORIGINAL CODE ------
  static final LabzenNOPLoggingEventBuilder SINGLETON = new LabzenNOPLoggingEventBuilder();
  private static final LabzenLogger LOGGER = Loggers.getLogger(LabzenNOPLoggingEventBuilder.class);

  private LabzenNOPLoggingEventBuilder() {
    super(LOGGER, Level.TRACE);
  }

  public static LabzenNOPLoggingEventBuilder singleton() {
    return SINGLETON;
  }
// --------------------------
// ------ NEW CODE ----------
  private static class Holder {
    static final LabzenNOPLoggingEventBuilder INSTANCE = new LabzenNOPLoggingEventBuilder();
  }

  private LabzenNOPLoggingEventBuilder() {
    // NOP Builder 不应依赖父类的实际日志功能，直接使用 null-safe 的最小化初始化
    super(new LabzenLogger(org.slf4j.LoggerFactory.getLogger(LabzenNOPLoggingEventBuilder.class)), Level.TRACE);
  }

  public static LabzenNOPLoggingEventBuilder singleton() {
    return Holder.INSTANCE;
  }
// --------------------------
```

---

#### 11. 逻辑缺陷 — LabzenLoggingEvent.getMessage() 与 LabzenLogger.log() 双重格式化 🟡

**Location:** `src/main/java/cn/labzen/logger/kernel/LabzenLoggingEvent.java#L102-L105` 和 `src/main/java/cn/labzen/logger/kernel/LabzenLogger.java#L207`

**Analysis:** `LabzenLoggingEvent.getMessage()` 调用了 `MessagePatternTileManager.transform()` 处理 Tile 占位符；同时 `LabzenLogger.mergeMarkersAndKeyValuePairs()` 又调用了 `MessageFormatter.arrayFormat()` 处理 `{}` 占位符。当消息中同时存在 Tile 占位符（如 `{name@number_0.00}`）和 `{}` 时，两者可能对同一内容进行重复处理，导致输出格式错误。两个格式化路径的职责边界不清晰。

**Fix Recommendation:** 在 `mergeMarkersAndKeyValuePairs()` 中不再调用 `MessageFormatter.arrayFormat()`，因为 `event.getMessage()` 已经由 Tile 系统完成了占位符替换。或者明确约定 `getMessage()` 仅返回原始模板，所有格式化统一由 `mergeMarkersAndKeyValuePairs()` 完成，消除双重格式化路径。
