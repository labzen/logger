package cn.labzen.logger.kernel.tile;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期格式化Tile，将日期/时间值按指定模式格式化输出。
 *
 * <p>占位符格式：{@code {param@date_pattern}}
 *
 * <p>支持的输入类型：
 * <ul>
 *   <li>Date - java.util.Date对象</li>
 *   <li>Long/Integer - 毫秒时间戳</li>
 *   <li>LocalDateTime - Java 8日期时间类型</li>
 *   <li>LocalDate - 仅日期</li>
 *   <li>LocalTime - 仅时间</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>
 * LocalDateTime now = LocalDateTime.now();
 * logger.info("时间:&lcub;@date_yyyy-MM-dd HH:mm:ss}", now);  // 输出：时间:2026-04-17 10:30:45
 * </pre>
 *
 * @see java.time.format.DateTimeFormatter
 * @see AbstractTile
 */
public class FormatterOfDateTile extends AbstractTile<String> {

  private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

  /**
   * 系统默认时区
   */
  private static final ZoneId ZONE = ZoneId.systemDefault();

  /**
   * 日期时间格式化器
   */
  private final DateTimeFormatter formatter;

  /**
   * 构造方法
   *
   * @param pattern 格式化模式，如"yyyy-MM-dd HH:mm:ss"
   */
  public FormatterOfDateTile(String pattern) {
    formatter = FORMATTER_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
  }

  /**
   * 格式化日期时间值
   *
   * <p>处理规则：
   * <ul>
   *   <li>null → "nil"</li>
   *   <li>Long/Integer → 转为LocalDateTime后格式化</li>
   *   <li>Date → 转为LocalDateTime后格式化</li>
   *   <li>LocalDateTime/LocalDate/LocalTime → 直接格式化</li>
   *   <li>其他类型 → "nil"</li>
   * </ul>
   *
   * @param value 输入值
   * @return 格式化后的字符串，null或不支持类型返回"nil"
   */
  @SuppressWarnings("DataFlowIssue")
  @Override
  public String convert(Object value) {
    if (value == null) {
      return "nil";
    } else if (value instanceof Long || value instanceof Integer) {
      return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) value), ZONE).format(formatter);
    } else if (value instanceof Date date) {
      return formatter.format(LocalDateTime.ofInstant(date.toInstant(), ZONE));
    } else if (value instanceof LocalDateTime || value instanceof LocalDate || value instanceof LocalTime) {
      return formatter.format((TemporalAccessor) value);
    }
    return "nil";
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FormatterOfDateTile that = (FormatterOfDateTile) o;
    return Objects.equals(formatter, that.formatter);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(formatter);
  }
}
