package cn.labzen.logger.kernel.tile

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.*

/**
 * 格式化占位，日期格式化占位，格式：{@date_yyyy-MM-dd}
 */
internal class FormatterOfDateTile(pattern: String) : Tile<String>() {

  private val formatter = DateTimeFormatter.ofPattern(pattern)

  override fun convert(value: Any?): String =
    when (value) {
      is Long,
      is Int ->
        LocalDateTime.ofInstant(Instant.ofEpochMilli(value as Long), zone).format(formatter)

      is Date -> formatter.format(LocalDateTime.ofInstant(value.toInstant(), zone))

      is LocalDate,
      is LocalTime,
      is LocalDateTime -> formatter.format(value as TemporalAccessor)

      else -> "nil"
    }

  override fun equals(other: Any?): Boolean =
    Objects.equals(other?.hashCode(), hashCode())

  override fun hashCode(): Int =
    Objects.hash(formatter)

  companion object {
    private val zone = ZoneId.systemDefault()
  }
}
