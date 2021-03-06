package cn.labzen.logger.config.loader

import cn.labzen.logger.config.ConfigurationLoader
import cn.labzen.logger.config.LabzenLoggerConfiguration
import org.yaml.snakeyaml.Yaml
import java.net.JarURLConnection
import java.util.*


internal class LabzenYAMLConfigurationLoader : ConfigurationLoader {

  private val yaml = Yaml()

  override fun load(): LabzenLoggerConfiguration? {
    val resource = this.javaClass.classLoader.getResource("labzen.yml") ?: return null
    val openedConnection = resource.openConnection()
    val isJarFile = openedConnection is JarURLConnection

    val yamlString = if (isJarFile) {
      val jarFile = (openedConnection as JarURLConnection).jarFile
      val labzenFile = jarFile.getJarEntry("labzen.yml")

      val data = ByteArray(labzenFile.size.toInt())
      jarFile.getInputStream(labzenFile).use { `is` ->
        `is`.read(data)
        String(data)
      }
    } else {
      openedConnection.getInputStream().use { `is` ->
        Scanner(`is`).use { scanner ->
          scanner.useDelimiter("\\A")
          scanner.next()
        }
      }
    } ?: return null

    val fragments: Iterable<Any> = yaml.loadAll(yamlString)

    return fragments.firstNotNullOfOrNull(::loadFromFragment)
  }

  private fun loadFromFragment(fragment: Any): LabzenLoggerConfiguration? {
    if (fragment !is Map<*, *>) {
      return null
    }

    val labzen = tryGetMap(fragment, "labzen") ?: return null
    val logger = tryGetMap(labzen, "logger") ?: return null

    val pattern = tryGetString(logger, "pattern")

    val levelSet = tryGetMap(logger, "level")
    val rootLevel = levelSet?.let { tryGetString(it, "root") }
    val levels = levelSet?.mapKeys { it.toString() }?.mapValues { it.toString() }

    val default = LabzenLoggerConfiguration()

    return LabzenLoggerConfiguration(
      pattern = pattern ?: default.pattern,
      rootLevel = rootLevel ?: default.rootLevel,
      levels = levels ?: default.levels
    )
  }

  private fun tryGetMap(map: Map<*, *>, key: String): Map<*, *>? =
    map[key]?.let {
      if (it is Map<*, *>) it else null
    }

  private fun tryGetList(map: Map<*, *>, key: String): List<*>? =
    map[key]?.let {
      if (it is List<*>) it else null
    }

  private fun tryGetString(map: Map<*, *>, key: String): String? =
    map[key]?.toString()

  private fun tryGetInt(map: Map<*, *>, key: String): Int? =
    map[key]?.let {
      try {
        it.toString().toInt()
      } catch (e: Exception) {
        null
      }
    }

  private fun tryGetDouble(map: Map<*, *>, key: String): Double? =
    map[key]?.let {
      try {
        it.toString().toDouble()
      } catch (e: Exception) {
        null
      }
    }

  private fun tryGetBoolean(map: Map<*, *>, key: String): Boolean? =
    map[key]?.let {
      try {
        it.toString().toBoolean()
      } catch (e: Exception) {
        null
      }
    }
}
