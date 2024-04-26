package cn.labzen.logger.kernel.tile.handle

import cn.labzen.logger.kernel.tile.*
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import java.util.concurrent.TimeUnit

object MessagePatternTileManager {

  // todo 缓存大小等参数，可做配置
  private val placeholders: LoadingCache<String, List<PlaceholderWrapper>> = Caffeine.newBuilder().maximumSize(4096)
    .expireAfterAccess(10, TimeUnit.MINUTES).build { key -> parsePlaceholders(key) }

  fun transform(pattern: String, args: List<Any?>?): String {
    val placeholders = placeholders.get(pattern)
    // 1TODO 缓存取
    // val parsed = parsePlaceholders(pattern)

    return formatMessage(pattern, placeholders, args)
  }

  private fun formatMessage(pattern: String, wrappers: List<PlaceholderWrapper>, values: List<Any?>?): String {
    if (wrappers.isEmpty()) {
      return pattern
    }

    val buffer = StringBuilder()

    var index = 0
    wrappers.forEach {
      for (i in index until it.startIndex) {
        buffer.append(pattern[i])
      }

      val convertedValue = tileConvert(it.firstTile, values)
      buffer.append(convertedValue)

      index = it.endIndex + 1
    }

    while (index < pattern.length) {
      buffer.append(pattern[index])
      index++
    }

    return buffer.toString()
  }

  private fun tileConvert(tile: HeadTile<*>?, values: List<Any?>?): String {
    tile ?: return ""

    var pointer: Tile<*>? = tile
    var converted: Any? = values
    while (pointer != null) {
      converted = pointer.convert(converted)
      pointer = pointer.getNext()
    }
    return converted?.toString() ?: ""
  }

  private fun parsePlaceholders(pattern: String): List<PlaceholderWrapper> {
    val placeholders = mutableListOf<PlaceholderWrapper>()
    var foundTimes = 0
    var i = 0

    while (i < pattern.length) {
      if (pattern[i] == '{') {
        val placeholder = findNextPlaceholder(pattern, i, foundTimes)
        if (placeholder != null) {
          placeholders.add(placeholder)
          foundTimes++
          i = placeholder.endIndex
        } else {
          i++
        }
      } else {
        i++
      }
    }

    return placeholders
  }

  private fun findNextPlaceholder(pattern: String, startIndex: Int, foundTimes: Int): PlaceholderWrapper? {
    if (startIndex > 0 && pattern[startIndex - 1] == '\\') {
      // 转义字符 \{ 不进行处理
      return null
    }

    val endIndex = pattern.indexOf('}', startIndex + 1)
    if (endIndex == -1) {
      return null
    }

    val internalText = pattern.substring(startIndex + 1, endIndex)
    val tiles = parsePlaceholderTiles(internalText, foundTimes)
    return PlaceholderWrapper(foundTimes, startIndex, endIndex, internalText, tiles)
    // val pw = findHeadTileAndWrapIt(pattern, startIndex, foundTimes) ?: return null
    //
    // val tileStartIndex = pw.internalText.indexOf('@', 0)
    // if (tileStartIndex >= 0) {
    //   findTiles(pw.internalText, tileStartIndex, pw.firstTile)
    // }
    //
    // return pw
  }

  // private fun findTiles(placeholderText: String, startIndex: Int, point: Tile<*>) {
  //   val nextTileStartIndex = placeholderText.indexOf('@', startIndex + 1)
  //   val endIndex = if (nextTileStartIndex < 0) {
  //     placeholderText.length
  //   } else {
  //     nextTileStartIndex
  //   }
  //
  //   val tileText = placeholderText.substring(startIndex, endIndex)
  //   val tile = Tile.match(tileText)
  //   val nextPoint = if (tile == null) {
  //     point
  //   } else {
  //     point.next = tile
  //     tile
  //   }
  //
  //   if (nextTileStartIndex < 0) {
  //     return
  //   } else {
  //     findTiles(placeholderText, nextTileStartIndex, nextPoint)
  //   }
  // }

  private fun parsePlaceholderTiles(text: String, foundTimes: Int): HeadTile<*> {
    val mightExistTiles = text.contains('@')
    return if (mightExistTiles) {
      parseFunctionTiles(text, foundTimes)
    } else {
      parseSimpleTile(text, foundTimes)
    }
  }

  private fun parseFunctionTiles(text: String, foundTimes: Int): HeadTile<*> {
    val firstTileStartIndex = text.indexOf('@')
    val tileHead: HeadTile<*> = if (firstTileStartIndex != 0) {
      val beforeText = text.substring(0, firstTileStartIndex)
      parseSimpleTile(beforeText, foundTimes)
    } else {
      DefaultTile(foundTimes)
    }

    parseFunctionTile(text, firstTileStartIndex, tileHead)

    return tileHead
  }

  private fun parseFunctionTile(text: String, startIndex: Int, prevTile: Tile<*>) {
    val nextTileStartIndex = text.indexOf('@', startIndex + 1)
    val endIndex = if (nextTileStartIndex < 0) {
      // 后面没有 Tile 了
      text.length
    } else {
      nextTileStartIndex
    }
    val tileText = text.substring(startIndex, endIndex)

    val tile = AbstractTile.match(tileText) ?: WrongTile("WRONG_TILE[$tileText]")
    prevTile.setNext(tile)

    if (nextTileStartIndex > 0) {
      parseFunctionTile(text, nextTileStartIndex, tile)
    }
  }

  private fun parseSimpleTile(text: String, foundTimes: Int): HeadTile<*> {
    return if (text.isBlank()) {
      DefaultTile(foundTimes)
    } else {
      try {
        val position = text.trim().toInt()
        PositionTile(position)
      } catch (e: Exception) {
        // ignore e
        NamedTile(text.trim())
      }
    }
  }

//   private fun findHeadTileAndWrapIt(pattern: String, startIndex: Int, foundTimes: Int): PlaceholderWrapper? {
//
//     // return if (endIndex < 0) {
//     //   null
//     // } else {
//     //   val internalText = pattern.substring(startIndex + 1, endIndex)
//
//     val firstTileStartIndex = internalText.indexOf('@')
//     val tileIdentifyText = if (firstTileStartIndex < 0) {
//       internalText
//     } else {
//       internalText.substring(0, firstTileStartIndex).trim()
//     }
//
//     if (tileIdentifyText.isBlank()) {
//       return PlaceholderWrapper(foundTimes, startIndex, endIndex, internalText, DefaultTile(foundTimes))
//     }
//
//     val head = try {
//       val position = tileIdentifyText.toInt()
//       PositionTile(position)
//     } catch (e: Exception) {
//       // ignore e
//       NamedTile(tileIdentifyText)
//     }
//     PlaceholderWrapper(foundTimes, startIndex, endIndex, internalText, head)
//   }
// }

  internal data class PlaceholderWrapper(
    val position: Int,
    val startIndex: Int,
    val endIndex: Int,
    val internalText: String,
    val firstTile: HeadTile<*>
  )
}
