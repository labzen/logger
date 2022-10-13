package cn.labzen.logger.kernel.marker

data class LabzenMarkerWrapper(
  internal val scene: SceneMarker? = null,
  internal val statusMarker: StatusMarker? = null,
  internal val tag: TagMarker? = null,
) : AbstractMarker() {

  override fun toString(): String {
    val sb = StringBuilder()
    scene?.let { sb.append(it.toString()).append(" ") }
    statusMarker?.let { sb.append(it.toString()).append(" ") }
    tag?.let { sb.append(it.toString()).append(" ") }
    val result = sb.toString()
    return if (result.isBlank()) {
      result
    } else {
      "$result>>"
    }
  }
}
