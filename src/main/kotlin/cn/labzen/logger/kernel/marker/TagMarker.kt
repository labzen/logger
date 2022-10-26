package cn.labzen.logger.kernel.marker

class TagMarker(private val texts: List<String>) : AbstractMarker() {

  override fun toString(): String =
    texts.joinToString(" ") {
      "[$it]"
    }
}
