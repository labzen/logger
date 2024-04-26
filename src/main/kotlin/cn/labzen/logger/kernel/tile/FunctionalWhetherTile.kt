package cn.labzen.logger.kernel.tile

import java.util.*

/**
 * 函数式占位，仅适用于boolean类型的参数，格式：{@whether_yes,no}
 */
internal class FunctionalWhetherTile(
  private val trueText: String,
  private val falseText: String
) : AbstractTile<String>() {

  override fun convert(value: Any?): String =
    if (value == true) trueText else falseText

  override fun equals(other: Any?): Boolean =
    Objects.equals(other?.hashCode(), hashCode())

  override fun hashCode(): Int =
    Objects.hash(trueText, falseText)
}
