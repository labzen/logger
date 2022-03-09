package cn.labzen.logger.core.marker

data class MarkerWrapper(
  internal val scene: SceneMarker? = null,
  internal val tag: TagMarker? = null,
  internal val code: CodeMarker? = null
) : AbstractMarker()
