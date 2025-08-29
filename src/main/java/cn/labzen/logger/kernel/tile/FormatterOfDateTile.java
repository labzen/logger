package cn.labzen.logger.kernel.tile;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Objects;

/**
 * 格式化占位，日期格式化占位，格式：&#123;@date_yyyy-MM-dd}
 */
public class FormatterOfDateTile extends AbstractTile<String> {

  private static final ZoneId ZONE = ZoneId.systemDefault();

  private final DateTimeFormatter formatter;

  public FormatterOfDateTile(String pattern) {
    formatter = DateTimeFormatter.ofPattern(pattern);
  }

  @SuppressWarnings("DataFlowIssue")
  @Override
  public String convert(Object value) {
    if (value == null) {
      return "nil";
    } else if (value instanceof Long || value instanceof Integer) {
      return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) value), ZONE).format(formatter);
    } else if (value instanceof Date date) {
      formatter.format(LocalDateTime.ofInstant(date.toInstant(), ZONE));
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
