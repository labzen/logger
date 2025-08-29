package cn.labzen.logger.kernel.tile;

public interface Tile<R> {

  void setNext(Tile<?> next);

  Tile<?> getNext();

  boolean hasNext();

  R convert(Object value);
}
