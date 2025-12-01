package cn.labzen.logger.kernel.marker;

import cn.labzen.logger.meta.LoggerConfiguration;
import cn.labzen.meta.Labzens;
import org.slf4j.Marker;

import java.util.ArrayList;
import java.util.List;

public class MarkerWrapper extends AbstractLimitedMarker {

  private static String tailed;

  private final ForcedMarker forced;
  private final SceneMarker scene;
  private final StatusMarker status;
  private final TagMarker tag;

  private List<Marker> references;

  static {
    LoggerConfiguration configuration = Labzens.configurationWith(LoggerConfiguration.class);
    MarkerWrapper.tailed = configuration.markerTailed();
  }

  public MarkerWrapper(ForcedMarker forced, SceneMarker scene, StatusMarker status, TagMarker tag) {
    this.forced = forced;
    this.scene = scene;
    this.status = status;
    this.tag = tag;
  }

  public boolean hasReferences() {
    return references != null && !references.isEmpty();
  }

  public void addReference(Marker marker) {
    if (marker == null) {
      return;
    }
    if (references == null) {
      references = new ArrayList<Marker>();
    }
    references.add(marker);
  }

  public ForcedMarker getForced() {
    return forced;
  }

  public SceneMarker getScene() {
    return scene;
  }

  public StatusMarker getStatus() {
    return status;
  }

  public TagMarker getTag() {
    return tag;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (scene != null) {
      sb.append(scene).append(" ");
    }
    if (status != null) {
      sb.append(status).append(" ");
    }
    if (tag != null) {
      sb.append(tag).append(" ");
    }
    String result = sb.toString();
    if (result.isBlank()) {
      return result;
    }
    return result + tailed;
  }
}
