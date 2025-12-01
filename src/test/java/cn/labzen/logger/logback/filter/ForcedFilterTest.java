package cn.labzen.logger.logback.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.spi.FilterReply;
import cn.labzen.logger.Loggers;
import cn.labzen.logger.kernel.marker.ForcedMarker;
import cn.labzen.logger.kernel.marker.MarkerWrapper;
import cn.labzen.logger.kernel.marker.SceneMarker;
import cn.labzen.meta.LabzenMetaInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForcedFilterTest {

  @BeforeAll
  static void init() {
    Loggers.enhance();
    new LabzenMetaInitializer().initialize(null);
  }

  @Test
  void testDecideWithForcedMarker() {
    ForcedFilter filter = new ForcedFilter();

    ForcedMarker forcedMarker = new ForcedMarker();
    Logger logger = mock(Logger.class);

    FilterReply reply = filter.decide(forcedMarker, logger, Level.INFO, "test message", new Object[0], null);
    assertEquals(FilterReply.ACCEPT, reply);
  }

  @Test
  void testDecideWithMarkerWrapperContainingForcedMarker() {
    ForcedFilter filter = new ForcedFilter();

    ForcedMarker forced = new ForcedMarker();
    SceneMarker scene = new SceneMarker("TEST_SCENE");
    MarkerWrapper wrapper = new MarkerWrapper(forced, scene, null, null);

    Logger logger = mock(Logger.class);

    FilterReply reply = filter.decide(wrapper, logger, Level.INFO, "test message", new Object[0], null);
    assertEquals(FilterReply.ACCEPT, reply);
  }

  @Test
  void testDecideWithMarkerWrapperWithoutForcedMarker() {
    ForcedFilter filter = new ForcedFilter();

    SceneMarker scene = new SceneMarker("TEST_SCENE");
    MarkerWrapper wrapper = new MarkerWrapper(null, scene, null, null);

    Logger logger = mock(Logger.class);

    FilterReply reply = filter.decide(wrapper, logger, Level.INFO, "test message", new Object[0], null);
    assertEquals(FilterReply.NEUTRAL, reply);
  }

  @Test
  void testDecideWithNullMarker() {
    ForcedFilter filter = new ForcedFilter();

    Logger logger = mock(Logger.class);

    FilterReply reply = filter.decide(null, logger, Level.INFO, "test message", new Object[0], null);
    assertEquals(FilterReply.NEUTRAL, reply);
  }

  @Test
  void testDecideWithOtherMarker() {
    ForcedFilter filter = new ForcedFilter();

    SceneMarker sceneMarker = new SceneMarker("OTHER_SCENE");
    Logger logger = mock(Logger.class);

    FilterReply reply = filter.decide(sceneMarker, logger, Level.INFO, "test message", new Object[0], null);
    assertEquals(FilterReply.NEUTRAL, reply);
  }
}
