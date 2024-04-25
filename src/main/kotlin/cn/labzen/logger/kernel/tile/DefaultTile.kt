package cn.labzen.logger.kernel.tile

import java.util.*

/**
 * 默认占位，格式：{}，兼容括号内存在空格的情况，如：{ }
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
