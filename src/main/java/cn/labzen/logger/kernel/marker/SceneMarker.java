package cn.labzen.logger.kernel.marker;

public class SceneMarker extends AbstractLimitedMarker {

  private final String text;

  public SceneMarker(String text) {
    this.text = text;
  }

  @Override
  public String toString() {
    return "<" + text + ">";
  }
}
