package cn.labzen.logger.core.pipe

object MessagePatternManager {

  fun transform(pattern: String, args: Any?): String {

    // 缓存取

    val parsed = parse(pattern)

    return format(pattern, parsed, args)
  }

  private fun format(pattern: String, wrappers: List<PlaceholderWrapper>, value: Any?): String {
    if (wrappers.isEmpty()) {
      return pattern
    }

    val buffer = StringBuilder()

    var index = 0
    wrappers.forEach {
      for (i in index until it.start) {
        buffer.append(pattern[i])
      }

      val convertedValue = tileConvert(buffer, it.head, value)
      buffer.append(convertedValue)

      index = it.end + 1
    }

    while (index < pattern.length) {
      buffer.append(pattern[index])
      index++
    }

    return buffer.toString()
  }

  private fun tileConvert(buf: StringBuilder, tile: Tile<*>?, value: Any?): String {
    tile ?: return ""

    var point = tile
    var converted: Any? = value
    while (point != null) {
      converted = point.convert(converted)
      point = point.next
    }
    return converted?.toString() ?: ""
  }

  private fun parse(pattern: String): List<PlaceholderWrapper> {
    val wrappers = mutableListOf<PlaceholderWrapper>()
    var foundTimes = 0
    var i = 0
    while (i < pattern.length) {
      if (pattern[i] == '{') {
        findPlaceholder(pattern, i, foundTimes)?.also {
          wrappers.add(it)
          // patternBuffer.append("")
          foundTimes++
          i = it.end
        } ?: run {
          i++
        }
      } else {
        i++
      }
    }

    return wrappers
  }

  private fun findPlaceholder(pattern: String, startIndex: Int, foundTimes: Int): PlaceholderWrapper? {
    if (startIndex > 0 && pattern[startIndex - 1] == '\\') {
      // 转义字符{
      return null
    }
    val pw = findHeadTileAndWrapIt(pattern, startIndex, foundTimes) ?: return null

    val tileStartIndex = pw.internalText.indexOf('@', 0)
    if (tileStartIndex >= 0) {
      findTiles(pw.internalText, tileStartIndex, pw.head)
    }

    return pw
  }

  private fun findTiles(placeholderText: String, startIndex: Int, point: Tile<*>) {
    val nextTileStartIndex = placeholderText.indexOf('@', startIndex + 1)
    val endIndex = if (nextTileStartIndex < 0) {
      placeholderText.length
    } else {
      nextTileStartIndex
    }

    val tileText = placeholderText.substring(startIndex, endIndex)
    val tile = Tile.match(tileText)
    val nextPoint = if (tile == null) {
      point
    } else {
      point.next = tile
      tile
    }

    if (nextTileStartIndex < 0) {
      return
    } else {
      findTiles(placeholderText, nextTileStartIndex, nextPoint)
    }
  }

  private fun findHeadTileAndWrapIt(pattern: String, startIndex: Int, foundTimes: Int): PlaceholderWrapper? {
    val endIndex = pattern.indexOf('}', startIndex + 1)
    return if (endIndex < 0) {
      null
    } else {
      val internalText = pattern.substring(startIndex + 1, endIndex)

      val nextTileIndex = internalText.indexOf('@')
      val tileIdentifyText = if (nextTileIndex < 0) {
        internalText.trim()
      } else {
        internalText.substring(0, nextTileIndex).trim()
      }

      if (tileIdentifyText.isBlank()) {
        return PlaceholderWrapper(foundTimes, startIndex, endIndex, internalText, DefaultTile(foundTimes))
      }

      val head = try {
        val position = tileIdentifyText.toInt()
        IndexedTile(position)
      } catch (e: Exception) {
        // ignore e
        NamedTile(tileIdentifyText)
      }
      PlaceholderWrapper(foundTimes, startIndex, endIndex, internalText, head)
    }
  }
}

data class PlaceholderWrapper(
  val index: Int,
  val start: Int,
  val end: Int,
  val internalText: String,
  val head: Tile<*>
)
