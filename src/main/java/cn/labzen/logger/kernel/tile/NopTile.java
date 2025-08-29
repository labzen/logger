package cn.labzen.logger.kernel.tile;

public class NopTile extends AbstractTile<Object> implements HeadTile<Object> {

  public static final NopTile INSTANCE = new NopTile();

  @Override
  public Object convert(Object value) {
    return "";
  }
}
