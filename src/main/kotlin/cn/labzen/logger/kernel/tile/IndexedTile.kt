package cn.labzen.logger.kernel.tile

/**
 * 指参占位，顺序占位，格式：{0}，与默认占位的实现原理相同
 */
internal class IndexedTile(index: Int) : DefaultTile(index), HeadTile
