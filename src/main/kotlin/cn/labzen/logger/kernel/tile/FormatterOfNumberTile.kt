package cn.labzen.logger.kernel.tile

import java.text.DecimalFormat
import java.util.*

/**
 * 格式化占位，数字格式化占位，格式：{@number_0.00}
 */
internal class FormatterOfNumberTile(pattern: String) : AbstractTile<String?>() {

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
