package cn.labzen.logger.kernel.tile

abstract class Tile<out R> {

  internal var next: Tile<*>? = null

  fun hasNext() = next != null

  abstract fun convert(value: Any?): R

  companion object {
    private val TILE_FORMAT_NUMBER_REGEX = Regex("^@number_(.*)$")
    private val TILE_FORMAT_DATE_REGEX = Regex("^@date_(.*)$")
    private val TILE_WRAP_REGEX = Regex("^@wrap_(.{2})$")
    private val TILE_WHETHER_REGEX = Regex("^@whether_(.*),(.*)$")
    private val TILE_WIDTH_REGEX = Regex("^@width_(\\d+)(,(\\d+))?$")

    fun match(tileText: String): Tile<*>? =
      when {
        tileText.matches(TILE_FORMAT_NUMBER_REGEX) -> {
          val result = TILE_FORMAT_NUMBER_REGEX.find(tileText)
          FormatterOfNumberTile(result!!.groupValues[1])
        }

        tileText.matches(TILE_FORMAT_DATE_REGEX) -> {
          val result = TILE_FORMAT_DATE_REGEX.find(tileText)
          FormatterOfDateTile(result!!.groupValues[1])
        }

        tileText.matches(TILE_WRAP_REGEX) -> {
          val result = TILE_WRAP_REGEX.find(tileText)
          FunctionalWrapTile(result!!.groupValues[1])
        }

        tileText.matches(TILE_WHETHER_REGEX) -> {
          val result = TILE_WHETHER_REGEX.find(tileText)
          FunctionalWhetherTile(result!!.groupValues[1], result.groupValues[2])
        }

        tileText.matches(TILE_WIDTH_REGEX) -> {
          val result = TILE_WIDTH_REGEX.find(tileText)
          val min = result!!.groupValues[1].toInt()
          val max = result.groupValues[3].let {
            if (it.isBlank()) {
              FunctionalWidthControlTile.UNDEFINED
            } else {
              it.toInt()
            }
          }
          FunctionalWidthControlTile(min, max)
        }

        else -> null
      }
  }
}
