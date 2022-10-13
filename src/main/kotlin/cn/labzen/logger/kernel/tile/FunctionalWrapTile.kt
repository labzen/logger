package cn.labzen.logger.kernel.tile

import java.util.*

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
