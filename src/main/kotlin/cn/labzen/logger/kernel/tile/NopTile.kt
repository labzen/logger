package cn.labzen.logger.kernel.tile

internal class NopTile : Tile<Any?>(), HeadTile {

  override fun convert(value: Any?) = ""

  companion object {
    val INSTANCE = NopTile()
  }
}
