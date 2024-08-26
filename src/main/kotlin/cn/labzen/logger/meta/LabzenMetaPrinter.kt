package cn.labzen.logger.meta

import cn.labzen.logger.kotlin.logger
import cn.labzen.meta.Labzens
import cn.labzen.meta.component.bean.Component
import cn.labzen.meta.component.bean.Information

internal object LabzenMetaPrinter {

  private val logger = logger { }

  fun print() {
    printLogoAndComponents()
    printSystemInformation()
  }

  private fun printLogoAndComponents() {
    val e = "\u001B[0m"
    val k = "\u001B[38;5;214m"
    val h = "\u001B[38;5;179m"
    val t = "\u001B[38;5;184m"

    logger.info("正在使用 'Labzen' 组件系列..")
    println("")
    println("$k█▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀$e")
    println("$k█$e")
    println("$k█$e  $h ██▓      ▄▄▄        ▄▄▄▄    ▒███████▒ ▓██████ ███▄     █$e")
    println("$k█$e  $h ██▒     ▒████▄     ▓█████▄  ░ ▒   ▄▀░ ▓█    ▀ ██ ▀█   ██$e")
    println("$k█$e  $h▒██░     ▒██  ▀█▄   ▒██  ▄██   ░ ▄▀ ░  ▒████   ██  ▀█  █▒$e")
    println("$k█$e  $h▒██░     ░██▄▄▄▄██  ▒██░█▀     ▄▀      ▒▓█  ▄  ██▒  ▐▌██▒$e")
    println("$k█$e  $h░█████▓▒  ▓█   ▓██  ░▓▒  ▀█▓ ▒███████▒ ░▒████▒ ██░   ▓██░$e")
    println("$k█$e  $h░ ▒░▓  ░  ▒▒   ▓▒█░ ░▒▓███▀▒ ░▒▒ ▓░▒░▒ ░░ ▒░ ░  ▒░   ▒ ▒ $e")
    println("$k█$e  $h░ ░ ▒  ░   ▒   ▒▒ ░  ░▒   ░  ░░▒ ▒ ░ ▒  ░ ░  ░  ░░   ░ ▒░$e")
    println("$k█$e  $h  ░ ░      ░   ▒     ░    ░  ░ ░ ░ ░ ░    ░      ░     ░ $e")
    println("$k█$e  $h    ░  ░       ░  ░  ░         ░ ░        ░  ░           $e")
    println("$k█$e")
    println("$k█$e $t Using Labzen Components$e")
    println("$k█$e")

    val infos = Labzens.components().values.map(Component::information)
      .sortedBy { information: Information -> information.title }
    if (infos.isEmpty()) {
      println("$k█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄$e")
      println()
      return
    }
    val maxLength = infos.maxOf { it.title.length }

    infos.forEach {
      val title = it.title.padEnd(maxLength, ' ')
      val versionWithColor = if (it.version.endsWith("-SNAPSHOT")) {
        "\u001B[38;5;167mv${it.version}\u001B[0m"
      } else {
        "\u001B[38;5;157mv${it.version}\u001B[0m"
      }
      println("$k█$e  :: \u001B[38;5;184m$title\u001B[0m :: $versionWithColor - ${it.description}")
    }
    println("$k█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄$e")
    println()
  }

  private fun printSystemInformation() {
    val allSystemInformation = Labzens.allSystemInformation().filter { it.description != null }

    val e = "\u001B[0m"
    val b = "\u001B[38;5;178m"
    val c = arrayOf("\u001B[38;5;107m", "\u001B[38;5;137m")

    // 加这个 1 代表的是序号后边那个点
    val indexLength = allSystemInformation.size.toString().length + 1
    val catalogMaxLength = allSystemInformation.maxOf { it.catalog.length }
    val nameMaxLength = allSystemInformation.maxOf { it.name.length }

    logger.info("检测到当前主机信息：")

    var lastCatalog = allSystemInformation.first().catalog
    var ci = 0
    allSystemInformation.forEachIndexed { index, si ->
      val indexString = "$index.".padStart(indexLength)
      val catalogString = si.catalog.padStart(catalogMaxLength)
      val nameString = si.name.padEnd(nameMaxLength)

      if (lastCatalog != si.catalog) {
        lastCatalog = si.catalog
        ci = ci xor 1
      }

      println("$b♎ $indexString ${c[ci]}[ $catalogString :: $nameString ]$e ${si.title}  >>>  ${si.description}")
    }

    println()
  }
}
