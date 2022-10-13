package cn.labzen.logger.kernel.tile

import java.util.*

/**
 * 指参占位，顺序占位，格式：{param_name}，日志参数必须是[Map]
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
