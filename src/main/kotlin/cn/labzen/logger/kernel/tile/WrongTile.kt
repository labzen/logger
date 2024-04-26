package cn.labzen.logger.kernel.tile

import java.util.*

/**
 * 错误的占位符，用于标记 log pattern 中 tile 使用错误
 */
internal class WrongTile(private val message: String) : AbstractTile<String?>() {

  override fun convert(value: Any?): String = message

  override fun equals(other: Any?): Boolean =
    Objects.equals(other?.hashCode(), hashCode())

  override fun hashCode(): Int =
    Objects.hash(message)
}
