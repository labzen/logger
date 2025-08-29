package cn.labzen.logger.kernel.marker;

import java.util.List;
import java.util.stream.Collectors;

public class TagMarker extends AbstractLimitedMarker {

  private final List<String> tags;

  public TagMarker(List<String> tags) {
    this.tags = tags;
  }

  @Override
  public String toString() {
    if (tags == null) {
      return "";
    }

    return tags.stream().map(t -> "[" + t + "]").collect(Collectors.joining(" "));
  }
}
