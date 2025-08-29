package cn.labzen.logger.kernel.tile;

import java.util.*;

/**
 * 默认占位，格式：{}，兼容括号内存在空格的情况，如：{}
 */
public class DefaultTile extends AbstractTile<Object> implements HeadTile<Object> {

  private final int position;

  public DefaultTile(int position) {
    this.position = position;
  }

  @Override
  public Object convert(Object value) {
    switch (value) {
      case Object[] array -> {
        if (array.length == 1 && array[0] instanceof List<?> list) {
          return safeTake(list, position);
        } else {
          return safeTake(Arrays.asList(array), position);
        }
      }
      case List<?> list -> {
        return safeTake(list, position);
      }
      case Map<?, ?> map -> {
        return safeTake(new ArrayList<>(map.values()), position);
      }
      case null, default -> {
        return position == 0 ? value : "";
      }
    }
  }

  private Object safeTake(List<?> list, int position) {
    return position < list.size() ? list.get(position) : null;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefaultTile that = (DefaultTile) o;
    return position == that.position;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(position);
  }
}
