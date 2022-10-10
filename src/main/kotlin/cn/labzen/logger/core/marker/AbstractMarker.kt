package cn.labzen.logger.core.marker

import org.slf4j.Marker

abstract class AbstractMarker : Marker {

  @Deprecated("Deprecated in Java", ReplaceWith("false"))
  override fun hasChildren(): Boolean = false

  override fun hasReferences(): Boolean = false

  override fun getName(): String {
    throw IllegalAccessException("unnecessary method")
  }

  override fun add(reference: Marker?) {
    throw IllegalAccessException("unnecessary method")
  }

  override fun remove(reference: Marker?): Boolean {
    throw IllegalAccessException("unnecessary method")
  }

  override fun iterator(): MutableIterator<Marker> {
    throw IllegalAccessException("unnecessary method")
  }

  override fun contains(other: Marker?): Boolean {
    throw IllegalAccessException("unnecessary method")
  }

  override fun contains(name: String?): Boolean {
    throw IllegalAccessException("unnecessary method")
  }
}
