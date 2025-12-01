package cn.labzen.logger.kernel.marker;

import cn.labzen.logger.Loggers;
import cn.labzen.logger.kernel.enums.Scenes;
import cn.labzen.logger.kernel.enums.Status;
import cn.labzen.meta.LabzenMetaInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkerTest {

  @BeforeAll
  static void init() {
    Loggers.enhance();
    new LabzenMetaInitializer().initialize(null);
  }

  @Test
  void testForcedMarker() {
    ForcedMarker marker = new ForcedMarker();
    assertNotNull(marker);
    // The getName method throws IllegalStateException as expected
    assertThrows(IllegalStateException.class, marker::getName);
  }

  @Test
  void testSceneMarker() {
    SceneMarker marker = new SceneMarker(Scenes.LOGIN.name());
    assertNotNull(marker);
    assertTrue(marker.toString().contains("LOGIN"));
  }

  @Test
  void testStatusMarker() {
    StatusMarker marker = new StatusMarker(Status.SUCCESS.getText());
    assertNotNull(marker);
    assertTrue(marker.toString().contains(Status.SUCCESS.getText()));
  }

  @Test
  void testTagMarker() {
    TagMarker marker = new TagMarker(List.of("tag1", "tag2", "tag3"));
    assertNotNull(marker);
    String name = marker.toString();
    assertTrue(name.contains("tag1"));
    assertTrue(name.contains("tag2"));
    assertTrue(name.contains("tag3"));
  }

  @Test
  void testMarkerWrapper() {
    ForcedMarker forced = new ForcedMarker();
    SceneMarker scene = new SceneMarker("TEST");
    StatusMarker status = new StatusMarker("OK");
    TagMarker tags = new TagMarker(List.of("tag1"));

    MarkerWrapper wrapper = new MarkerWrapper(forced, scene, status, tags);
    assertNotNull(wrapper);
    assertEquals(forced, wrapper.getForced());
    assertEquals(scene, wrapper.getScene());
    assertEquals(status, wrapper.getStatus());
    assertEquals(tags, wrapper.getTag());
  }

  @Test
  void testMarkerWrapperWithNulls() {
    MarkerWrapper wrapper = new MarkerWrapper(null, null, null, null);
    assertNotNull(wrapper);
    assertNull(wrapper.getForced());
    assertNull(wrapper.getScene());
    assertNull(wrapper.getStatus());
    assertNull(wrapper.getTag());
  }

  @Test
  void testMarkerWrapperPartial() {
    SceneMarker scene = new SceneMarker("PARTIAL");
    TagMarker tags = new TagMarker(List.of("partial"));

    MarkerWrapper wrapper = new MarkerWrapper(null, scene, null, tags);
    assertNotNull(wrapper);
    assertNull(wrapper.getForced());
    assertNotNull(wrapper.getScene());
    assertNull(wrapper.getStatus());
    assertNotNull(wrapper.getTag());
  }

  @Test
  void testSceneMarkerWithSpecialCharacters() {
    SceneMarker marker = new SceneMarker("SCENE_WITH_SPECIAL_!@#$%");
    assertNotNull(marker);
    assertFalse(marker.toString().isEmpty());
  }

  @Test
  void testStatusMarkerWithSpecialCharacters() {
    StatusMarker marker = new StatusMarker("STATUS_WITH_SPECIAL_!@#$%");
    assertNotNull(marker);
    assertFalse(marker.toString().isEmpty());
  }

  @Test
  void testTagMarkerWithManyTags() {
    List<String> manyTags = List.of("tag1", "tag2", "tag3", "tag4", "tag5",
        "tag6", "tag7", "tag8", "tag9", "tag10");
    TagMarker marker = new TagMarker(manyTags);
    assertNotNull(marker);
    String name = marker.toString();
    assertTrue(name.contains("tag1"));
    assertTrue(name.contains("tag10"));
  }
}
