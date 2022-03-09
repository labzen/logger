package cn.labzen.logger.core.pipe

import java.text.DecimalFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.*

// todo 有没有做性能优化的可能
abstract class Tile<out R> {

  internal var next: Tile<*>? = null

  fun hasNext() = next != null

  abstract fun convert(value: Any?): R

  companion object {
    private val TILE_FORMAT_NUMBER_REGEX = Regex("^@number_(.*)$")
    private val TILE_FORMAT_DATE_REGEX = Regex("^@date_(.*)$")
    private val TILE_WRAP_REGEX = Regex("^@wrap_(.{2})$")
    private val TILE_WHETHER_REGEX = Regex("^@whether_(.*),(.*)$")
    private val TILE_WIDTH_REGEX = Regex("^@width_(\\d+)(,(\\d+))?$")

    fun match(tileText: String): Tile<*>? =
      when {
        tileText.matches(TILE_FORMAT_NUMBER_REGEX) -> {
          val result = TILE_FORMAT_NUMBER_REGEX.find(tileText)
          FormatterOfNumberTile(result!!.groupValues[1])
        }
        tileText.matches(TILE_FORMAT_DATE_REGEX) -> {
          val result = TILE_FORMAT_DATE_REGEX.find(tileText)
          FormatterOfDateTile(result!!.groupValues[1])
        }
        tileText.matches(TILE_WRAP_REGEX) -> {
          val result = TILE_WRAP_REGEX.find(tileText)
          FunctionalWrapTile(result!!.groupValues[1])
        }
        tileText.matches(TILE_WHETHER_REGEX) -> {
          val result = TILE_WHETHER_REGEX.find(tileText)
          FunctionalWhetherTile(result!!.groupValues[1], result.groupValues[2])
        }
        tileText.matches(TILE_WIDTH_REGEX) -> {
          val result = TILE_WIDTH_REGEX.find(tileText)
          val min = result!!.groupValues[1].toInt()
          val max = result.groupValues[3].let {
            if (it.isBlank()) {
              WidthControlTile.UNDEFINED
            } else {
              it.toInt()
            }
          }
          WidthControlTile(min, max)
        }
        else -> null
      }
  }
}

interface HeadTile

/**
 * 默认占位，格式：{}
 */
internal open class DefaultTile(private val position: Int) : Tile<Any?>(), HeadTile {

  override fun convert(value: Any?): Any? {
    return when (value) {
      is Array<*> -> {
        if (value.size == 1 && value[0] is List<*>) {
          (value[0] as List<*>).safeTake(position)
        } else {
          value.toList().safeTake(position)
        }
      }
      is List<*> -> value.safeTake(position)
      is Map<*, *> -> value.values.toList().safeTake(position)
      else -> if (position == 0) {
        value
      } else {
        ""
      }
    }
  }

  private fun List<*>.safeTake(position: Int): Any? =
    if (position < this.size) {
      this[position]
    } else {
      null
    }

  override fun equals(other: Any?): Boolean =
    Objects.equals(other?.hashCode(), hashCode())

  override fun hashCode(): Int =
    Objects.hash(position)
}

/**
 * 指参占位，顺序占位，格式：{0}，与默认占位的实现原理相同
 */
internal class IndexedTile(index: Int) : DefaultTile(index), HeadTile

/**
 * 指参占位，顺序占位，格式：{param_name}
 */
internal class NamedTile(private val key: String) : Tile<Any?>(), HeadTile {

  /**
   * @param value must be Map<String, Any?>
   */
  override fun convert(value: Any?): Any? =
    if (value is Array<*> && value.size == 1 && value[0] is Map<*, *>) {
      (value[0] as Map<*, *>)[key]
    } else {
      null
    }

  override fun equals(other: Any?): Boolean =
    Objects.equals(other?.hashCode(), hashCode())

  override fun hashCode(): Int =
    Objects.hash(key)
}

/**
 * 格式化占位，数字格式化占位，格式：{@date_0.00}
 */
internal class FormatterOfNumberTile(pattern: String) : Tile<String?>() {

  private val formatter = DecimalFormat(pattern)

  override fun convert(value: Any?): String? =
    if (value is Number) {
      formatter.format(value)
    } else {
      value?.toString()
    }

  override fun equals(other: Any?): Boolean =
    Objects.equals(other?.hashCode(), hashCode())

  override fun hashCode(): Int =
    Objects.hash(formatter)
}

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

/**
 * 函数式占位，将参数使用给定的字符包裹输出，格式：{@wrap_[]}
 */
internal class FunctionalWrapTile(private val edge: String) : Tile<String?>() {

  init {
    if (edge.length != 2) {
      throw IllegalArgumentException("requires 2 characters")
    }
  }

  override fun convert(value: Any?): String? =
    value?.let {
      "${edge[0]}$it${edge[1]}"
    }

  override fun equals(other: Any?): Boolean =
    Objects.equals(other?.hashCode(), hashCode())

  override fun hashCode(): Int =
    Objects.hash(edge)
}

/**
 * 函数式占位，仅适用于boolean类型的参数，格式：{@whether_yes,no}
 */
internal class FunctionalWhetherTile(
  private val trueText: String,
  private val falseText: String
) : Tile<String>() {

  override fun convert(value: Any?): String =
    if (value == true) trueText else falseText

  override fun equals(other: Any?): Boolean =
    Objects.equals(other?.hashCode(), hashCode())

  override fun hashCode(): Int =
    Objects.hash(trueText, falseText)
}

internal class WidthControlTile(
  private val min: Int = UNDEFINED,
  private val max: Int = UNDEFINED
) : Tile<String?>() {

  init {
    if (min == UNDEFINED && max == UNDEFINED) {
      throw IllegalArgumentException("at least 1 parameter is defined")
    }
  }

  override fun convert(value: Any?): String? =
    value?.toString()?.let {
      when {
        min == UNDEFINED -> if (it.length > max) it.subSequence(0, max).toString() else it
        max == UNDEFINED -> if (it.length < min) it.padEnd(min) else it
        else -> {
          when {
            it.length > max -> it.subSequence(0, max)
              .toString()
            it.length < min -> it.padEnd(min)
            else -> it
          }
        }
      }
    }

  override fun equals(other: Any?): Boolean =
    Objects.equals(other?.hashCode(), hashCode())

  override fun hashCode(): Int =
    Objects.hash(min, max)

  companion object {
    internal const val UNDEFINED = -1
  }
}
