package cn.labzen.logger.kernel.tile;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 指参占位，顺序占位，格式：{param_name}，日志参数必须是Map
 */
public class NamedTile extends AbstractTile<Object> implements HeadTile<Object> {

  private final String key;

  public NamedTile(String key) {
    this.key = key;
  }

  @Override
  public Object convert(Object value) {
    if (value instanceof List<?> list && list.size() == 1 && list.getFirst() instanceof Map<?, ?> first) {
      return first.get(key);
    }
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NamedTile namedTile = (NamedTile) o;
    return Objects.equals(key, namedTile.key);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key);
  }
}
