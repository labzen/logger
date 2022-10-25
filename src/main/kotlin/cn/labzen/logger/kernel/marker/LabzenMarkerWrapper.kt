package cn.labzen.logger.kernel.marker

import org.slf4j.Marker

data class LabzenMarkerWrapper(
  internal val forced: ForcedMarker? = null,
  internal val scene: SceneMarker? = null,
  internal val statusMarker: StatusMarker? = null,
  internal val tag: TagMarker? = null,
) : AbstractMarker() {

  private var referenceList: MutableList<Marker>? = null

  override fun hasReferences(): Boolean =
    referenceList?.isNotEmpty() ?: false

  override fun add(reference: Marker?) {
    reference ?: return

    if (referenceList == null) {
      addAll(listOf(reference))
    } else {
      referenceList!!.add(reference)
    }
  }

  internal fun addAll(reference: List<Marker>) {
    referenceList = reference.toMutableList()
  }

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
