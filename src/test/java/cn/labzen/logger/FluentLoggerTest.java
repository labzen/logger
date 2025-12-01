package cn.labzen.logger;

import cn.labzen.logger.kernel.LabzenLogger;
import cn.labzen.logger.kernel.enums.Scenes;
import cn.labzen.logger.kernel.enums.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class FluentLoggerTest {

  private static LabzenLogger logger;

  @BeforeAll
  static void init() {
    //Loggers.enhance();
    logger = Loggers.getLogger(FluentLoggerTest.class);
  }

  @Test
  void testDecide() {
    System.out.println("             >>>>>> : there is log message wouldn't be output");
    logger.atInfo().conditional(false).log("disabled log message");
    System.out.println();

    System.out.println("             >>>>>> : there is log message depend on 'conditional()' method");
    logger.atInfo().conditional(true).log("log message");
  }

  @Test
  void testForce() {
    System.out.println("             >>>>>> : there is log debug message should be output");
    logger.atDebug().force().log("log forced message");
  }

  @Test
  void testScene() {
    System.out.println("             >>>>>> : should be output: '{◆ COMPLETE} log message with complete scene'");
    logger.atInfo().status(Status.SUCCESS).log("log message with complete scene");

    System.out.println("             >>>>>> : should be output: '{✈ TODO} log message with todo scene'");
    logger.atWarn().status(Status.TODO).log("log message with todo scene");

    logger.atWarn().status(Status.FIXME).log("没有Controller的方法实现，未指定ServiceHandler的main参数");
  }

  @Test
  void testTag() {
    System.out.println(
        "             >>>>>> : should be output: 'Spring Mybatis log message with tag of [Spring, Mybatis]'");
    logger.atInfo().tags("Spring", "Mybatis").log("log message with tag of [Spring, Mybatis]");
  }

  @Test
  void testPhase() {

  }

  @Test
  void testPlaceholderParameter() {
    System.out.println("             >>>>>> : should be output: 'this are two numbers: 1, 2'");
    logger.atInfo().log("this are two numbers: {}, {}", 1, 2);
    System.out.println("             >>>>>> : should be output: 'this are two numbers: 2, 1'");
    logger.atInfo().log("this are two numbers: {1}, {0}", 1, 2);

    Map<String, Integer> numbers = new HashMap<>();
    numbers.put("one", 1);
    numbers.put("two", 2);
    System.out.println("             >>>>>> : should be output: 'this are two numbers: 1, 2'");
    logger.atInfo().log("this are two numbers: {one}, {two}", numbers);
  }

  @Test
  void testTiledPlaceholderMessage() {
    System.out.println("             >>>>>> : should be output: 'this is number format text: 1.20'");
    logger.atInfo().log("this is number format text: {@number_0.00}", 1.2);
    System.out.println("             >>>>>> : should be output: 'this is number format text: 1.2'");
    logger.atInfo().log("this is number format text: {@number_0.##}", 1.2);

    System.out.println("========================================================");

    LocalDateTime dt = LocalDateTime.of(2021, 1, 1, 12, 30, 0);
    LocalDate d = LocalDate.of(2021, 1, 1);
    LocalTime t = LocalTime.of(12, 30, 0);
    System.out.println("             >>>>>> : should be output: 'this is date format text: 2021年01月01日 12:30:00'");
    logger.atInfo().log("this is date format text: {@date_yyyy年MM月dd日 HH:mm:ss}", dt);
    System.out.println("             >>>>>> : should be output: 'this is date format text: 2021年01月01日'");
    logger.atInfo().log("this is date format text: {@date_yyyy年MM月dd日}", d);
    System.out.println("             >>>>>> : should be output: 'this is date format text: 12:30:00'");
    logger.atInfo().log("this is date format text: {@date_HH:mm:ss}", t);

    System.out.println("========================================================");

    System.out.println("             >>>>>> : should be output: 'this is wrap processed text: <text>'");
    logger.atInfo().log("this is wrap processed text: {@wrap_<>}", "text");

    System.out.println("========================================================");

    System.out.println("             >>>>>> : should be output: 'this is whether processed text: yes'");
    logger.atInfo().log("this is whether processed text: {@whether_yes,no}", true);
    System.out.println("             >>>>>> : should be output: 'this is whether processed text: no'");
    logger.atInfo().log("this is whether processed text: {@whether_yes,no}", false);

    System.out.println("========================================================");

    System.out.println("             >>>>>> : should be output: 'this is fixed width text: [1234567890  ]'");
    logger.atInfo().log("this is fixed width text: [{@width_12}]", "1234567890");
    System.out.println("             >>>>>> : should be output: 'this is fixed width text: [1234567890]'");
    logger.atInfo().log("this is fixed width text: [{@width_5}]", "1234567890");
    System.out.println("             >>>>>> : should be output: 'this is fixed width text: [12345]'");
    logger.atInfo().log("this is fixed width text: [{@width_3,5}]", "1234567890");
  }

  //@Test
  //void testCalculatedMessage() {
  //  System.out.println("             >>>>>> : should be output: 'this is simple calculated text: 123'");
  //  logger.atInfo().logCalculated("this is simple calculated text: {}", () -> {
  //    int calculatedValue = 123;
  //    return String.valueOf(calculatedValue);
  //  });
  //
  //  System.out.println("             >>>>>> : should be output: 'this is list calculated text: 1, 2'");
  //  logger.atInfo().logCalculated("this is list calculated text: {}, {}", () -> List.of(1, 2));
  //
  //  System.out.println("             >>>>>> : should be output: 'this is map calculated text: 1, 2'");
  //  logger.atInfo().logCalculated("this is map calculated text: {one}, {two}", () -> {
  //    Map<String, Integer> numbers = new HashMap<>();
  //    numbers.put("one", 1);
  //    numbers.put("two", 2);
  //    return numbers;
  //  });
  //}

  @Test
  void testLogJson() throws URISyntaxException, IOException {
    URL resource = this.getClass().getClassLoader().getResource("test.json");
    Assertions.assertNotNull(resource);

    String json = Files.readString(Paths.get(resource.toURI()));
    logger.atInfo().status(Status.REMIND).tags("java", "kotlin").json(json).log("there is JSON text");
  }

  @Test
  void testLogXml() throws URISyntaxException, IOException {
    URL resource = this.getClass().getClassLoader().getResource("test.xml");
    Assertions.assertNotNull(resource);

    String xml = Files.readString(Paths.get(resource.toURI()));
    logger.atDebug().xml(xml).log("there is XML text");
  }

  @Test
  void testLogYaml() throws URISyntaxException, IOException {
    URL resource = this.getClass().getClassLoader().getResource("test.yaml");
    Assertions.assertNotNull(resource);

    String yml = Files.readString(Paths.get(resource.toURI()));
    logger.atDebug().yaml(yml).log("there is YAML text");
  }

  @Test
  void testLogError() {
    logger.atError().status(Status.DANGER).scene("测试").setCause(new RuntimeException("no message exception")).log();
    logger.atError().setCause(new RuntimeException("a runtime exception is thrown here")).log("exception occurred");
    logger.atError()
          .status(Status.FIXME)
          .setCause(new RuntimeException("a runtime exception is thrown here"))
          .log("exception occurred");
  }

  @Test
  void testComplexMessage() {

  }

  @Test
  void testPrefixMessage() {
    logger.atInfo().startPrefix(" --- ").log("接下来的日志会有前缀");
    logger.atInfo().scene(Scenes.LISTENER).log("第 {} 条日志", 1);
    logger.atWarn().scene(Scenes.FILTER).status(Status.FIXME).log("第 {} 条日志", 2);
    logger.atWarn().scene(Scenes.JOB).status(Status.NOTE).tags("Labzen").log("第 {} 条日志", 3);
    logger.atDebug().log("第 {} 条日志", 4);
    logger.atInfo().endPrefix(true).log("带有前缀的日志结束了");
  }

}
