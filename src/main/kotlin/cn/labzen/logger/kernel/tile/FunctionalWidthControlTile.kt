package cn.labzen.logger.kernel.tile

import java.util.*

internal class FunctionalWidthControlTile(
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
