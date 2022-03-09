package cn.labzen.logger.core.marker

data class CodeMarker(val type: CodeType, val text: String) : AbstractMarker() {

  // 还可扩展YAML, INI, JAVA等语言
  enum class CodeType {
    JSON, XML
  }
}
