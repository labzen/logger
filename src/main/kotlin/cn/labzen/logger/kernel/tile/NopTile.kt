package cn.labzen.logger.kernel.tile

internal class NopTile : AbstractTile<Any?>(), HeadTile<Any?> {

  override fun convert(value: Any?) = ""

  companion object {
    val INSTANCE = NopTile()
  }
}
