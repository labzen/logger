package cn.labzen.logger.kernel;

import cn.labzen.logger.Loggers;
import cn.labzen.logger.kernel.enums.CodeTypes;
import cn.labzen.logger.kernel.enums.Status;
import cn.labzen.meta.LabzenMetaInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.event.Level;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LabzenLoggingEventBuilderTest {

  private static LabzenLogger logger;

  @BeforeAll
  static void init() {
    Loggers.enhance();
    new LabzenMetaInitializer().initialize(null);
    logger = Loggers.getLogger(LabzenLoggingEventBuilderTest.class);
  }

  @Test
  void testForce() {
    LabzenLoggingEventBuilder builder = logger.atDebug().force();
    assertNotNull(builder);
    builder.log("forced debug log should be printed");
  }

  @Test
  void testConditional() {
    LabzenLoggingEventBuilder builder1 = logger.atInfo().conditional(true);
    assertNotNull(builder1);
    builder1.log("conditional true should print");

    LabzenLoggingEventBuilder builder2 = logger.atInfo().conditional(false);
    assertNotNull(builder2);
    builder2.log("conditional false should not print");
  }

  @Test
  void testConditionalWithSupplier() {
    LabzenLoggingEventBuilder builder1 = logger.atInfo().conditional(() -> true);
    assertNotNull(builder1);
    builder1.log("conditional supplier true");

    LabzenLoggingEventBuilder builder2 = logger.atInfo().conditional(() -> false);
    assertNotNull(builder2);
    builder2.log("conditional supplier false");
  }

  @Test
  void testInProfile() {
    LabzenLoggingEventBuilder builder = logger.atInfo().inProfile("dev", "test");
    assertNotNull(builder);
    builder.log("in profile dev or test");
  }

  @Test
  void testOutProfile() {
    LabzenLoggingEventBuilder builder = logger.atInfo().outProfile("prod");
    assertNotNull(builder);
    builder.log("not in profile prod");
  }

  @Test
  void testScene() {
    LabzenLoggingEventBuilder builder = logger.atInfo().scene("CUSTOM_SCENE");
    assertNotNull(builder);
    builder.log("scene with string");
  }

  @Test
  void testStatus() {
    LabzenLoggingEventBuilder builder1 = logger.atInfo().status(Status.SUCCESS);
    assertNotNull(builder1);
    builder1.log("status with enum");

    LabzenLoggingEventBuilder builder2 = logger.atInfo().status("CUSTOM_STATUS");
    assertNotNull(builder2);
    builder2.log("status with string");
  }

  @Test
  void testTags() {
    LabzenLoggingEventBuilder builder1 = logger.atInfo().tags("tag1", "tag2", "tag3");
    assertNotNull(builder1);
    builder1.log("tags with varargs");

    LabzenLoggingEventBuilder builder2 = logger.atInfo().tags(List.of("tag4", "tag5"));
    assertNotNull(builder2);
    builder2.log("tags with list");
  }

  @Test
  void testCounting() {
    LabzenLoggingEventBuilder builder = logger.atInfo().counting();
    assertNotNull(builder);
    builder.log("counting feature (not implemented yet)");
  }

  @Test
  void testPhase() {
    LabzenLoggingEventBuilder builder1 = logger.atInfo().phaseStart();
    assertNotNull(builder1);
    builder1.log("phase start");

    LabzenLoggingEventBuilder builder2 = logger.atInfo().phasePause();
    assertNotNull(builder2);
    builder2.log("phase pause");

    LabzenLoggingEventBuilder builder3 = logger.atInfo().phaseEnd();
    assertNotNull(builder3);
    builder3.log("phase end");
  }

  @Test
  void testJson() {
    String json = "{\"key\":\"value\"}";
    LabzenLoggingEventBuilder builder = logger.atInfo().json(json);
    assertNotNull(builder);
    builder.log("json data");
  }

  @Test
  void testXml() {
    String xml = "<root><item>value</item></root>";
    LabzenLoggingEventBuilder builder = logger.atInfo().xml(xml);
    assertNotNull(builder);
    builder.log("xml data");
  }

  @Test
  void testYaml() {
    String yaml = "key: value\nitems:\n  - item1\n  - item2";
    LabzenLoggingEventBuilder builder = logger.atInfo().yaml(yaml);
    assertNotNull(builder);
    builder.log("yaml data");
  }

  @Test
  void testPrefix() {
    LabzenLoggingEventBuilder builder1 = logger.atInfo().startPrefix("PREFIX-");
    assertNotNull(builder1);
    builder1.log("start prefix");

    logger.atInfo().log("with prefix");

    LabzenLoggingEventBuilder builder2 = logger.atInfo().endPrefix();
    assertNotNull(builder2);
    builder2.log("end prefix");
  }

  @Test
  void testPrefixImmediate() {
    LabzenLoggingEventBuilder builder1 = logger.atInfo().startPrefix("IMM-", true);
    assertNotNull(builder1);
    builder1.log("immediate start prefix");

    LabzenLoggingEventBuilder builder2 = logger.atInfo().endPrefix(true);
    assertNotNull(builder2);
    builder2.log("immediate end prefix");
  }

  @Test
  void testLogVariants() {
    logger.atInfo().log();
    logger.atInfo().log("simple message");
    logger.atInfo().log("message with arg {}", "value");
    logger.atInfo().log("message with two args {} {}", "val1", "val2");
    logger.atInfo().log("message with args", "arg1", "arg2", "arg3");
    logger.atInfo().log(() -> "message from supplier");
  }

  @Test
  void testCombinedFeatures() {
    logger.atInfo()
        .scene("CONTROLLER")
        .status(Status.SUCCESS)
        .tags("integration", "test")
        .json("{\"result\":\"ok\"}")
        .log("combined features test");

    logger.atWarn()
        .scene("SERVICE")
        .status(Status.FIXME)
        .tags("warning")
        .conditional(true)
        .log("warning with conditions");

    logger.atError()
        .scene("ERROR_SCENE")
        .status(Status.DANGER)
        .setCause(new RuntimeException("test exception"))
        .log("error with exception");
  }

  @Test
  void testNullAndEmptyValues() {
    logger.atInfo().scene((String) null).log("null scene");
    logger.atInfo().status((String) null).log("null status");
    logger.atInfo().tags(new String[0]).log("empty tags");
    logger.atInfo().json("").log("empty json");
    logger.atInfo().xml("").log("empty xml");
    logger.atInfo().yaml("").log("empty yaml");
  }
}
