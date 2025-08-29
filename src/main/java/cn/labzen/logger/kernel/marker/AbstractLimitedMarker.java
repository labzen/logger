package cn.labzen.logger.kernel.marker;

import org.slf4j.Marker;

import java.util.Iterator;

public abstract class AbstractLimitedMarker implements Marker {

  @Override
  public String getName() {
    throw new IllegalStateException("unnecessary method");
  }

  @Override
  public void add(Marker reference) {
    throw new IllegalStateException("unnecessary method");
  }

  @Override
  public boolean remove(Marker reference) {
    throw new IllegalStateException("unnecessary method");
  }

  @Override
  public boolean hasChildren() {
    return false;
  }

  @Override
  public boolean hasReferences() {
    return false;
  }

  @Override
  public Iterator<Marker> iterator() {
    throw new IllegalStateException("unnecessary method");
  }

  @Override
  public boolean contains(Marker other) {
    throw new IllegalStateException("unnecessary method");
  }

  @Override
  public boolean contains(String name) {
    throw new IllegalStateException("unnecessary method");
  }
}
