package cn.labzen.logger.kernel.tile

interface Tile<out R> {

  fun setNext(tile: Tile<*>)

  fun getNext(): Tile<*>?

  fun hasNext(): Boolean

  fun convert(value: Any?): R
}
