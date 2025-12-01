package cn.labzen.logger.print;

import cn.labzen.logger.Loggers;
import cn.labzen.logger.kernel.LabzenLogger;
import cn.labzen.logger.meta.LoggerConfiguration;
import cn.labzen.meta.Labzens;
import cn.labzen.meta.component.bean.ComponentMeta;
import cn.labzen.meta.component.bean.Information;
import cn.labzen.meta.system.SystemInformation;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Comparator;
import java.util.List;

public class MetaPrinter {

  private static final LabzenLogger logger = Loggers.getLogger(MetaPrinter.class);
  private static final String LABZEN_TEXT = "Labzen";

  public static void print() {
    LoggerConfiguration configuration = Labzens.configurationWith(LoggerConfiguration.class);
    assert configuration != null;

    // 打印 Labzen 模组信息
    if (configuration.printBanner()) {
      printBannerAndComponents();
    }

    if (configuration.printProjectInformation()) {
      printProjectInformation();
    }

    // 打印系统硬件信息
    if (configuration.printSystemInformation()) {
      printSystemInformation();
    }
  }

  private static void printBannerAndComponents() {
    String e = "\u001B[0m";
    String k = "\u001B[38;5;214m";
    String h = "\u001B[38;5;179m";
    String t = "\u001B[38;5;184m";

    System.out.println();
    System.out.printf(
        "%s█▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀%s%n",
        k,
        e);
    System.out.printf("%s█%s%n", k, e);
    System.out.printf("%s█%s  %s ██▓      ▄▄▄        ▄▄▄▄    ▒███████▒ ▓██████ ███▄     █%s%n", k, e, h, e);
    System.out.printf("%s█%s  %s ██▒     ▒████▄     ▓█████▄  ░ ▒   ▄▀░ ▓█    ▀ ██ ▀█   ██%s%n", k, e, h, e);
    System.out.printf("%s█%s  %s▒██░     ▒██  ▀█▄   ▒██  ▄██   ░ ▄▀ ░  ▒████   ██  ▀█  █▒%s%n", k, e, h, e);
    System.out.printf("%s█%s  %s▒██░     ░██▄▄▄▄██  ▒██░█▀     ▄▀      ▒▓█  ▄  ██▒  ▐▌██▒%s%n", k, e, h, e);
    System.out.printf("%s█%s  %s░█████▓▒  ▓█   ▓██  ░▓▒  ▀█▓ ▒███████▒ ░▒████▒ ██░   ▓██░%s%n", k, e, h, e);
    System.out.printf("%s█%s  %s░ ▒░▓  ░  ▒▒   ▓▒█░ ░▒▓███▀▒ ░▒▒ ▓░▒░▒ ░░ ▒░ ░  ▒░   ▒ ▒ %s%n", k, e, h, e);
    System.out.printf("%s█%s  %s░ ░ ▒  ░   ▒   ▒▒ ░  ░▒   ░  ░░▒ ▒ ░ ▒  ░ ░  ░  ░░   ░ ▒░%s%n", k, e, h, e);
    System.out.printf("%s█%s  %s  ░ ░      ░   ▒     ░    ░  ░ ░ ░ ░ ░    ░      ░     ░ %s%n", k, e, h, e);
    System.out.printf("%s█%s  %s    ░  ░       ░  ░  ░         ░ ░        ░  ░           %s%n", k, e, h, e);
    System.out.printf("%s█%s%n", k, e);
    System.out.printf("%s█%s %s Using Labzen Components%s%n", k, e, t, e);
    System.out.printf("%s█%s%n", k, e);

    List<Information> infos = Labzens.getComponentMetas()
                                     .values()
                                     .stream()
                                     .map(ComponentMeta::information)
                                     .sorted(Comparator.comparing(Information::title))
                                     .toList();
    int maxLength = infos.stream().mapToInt(inf -> inf.title().length()).max().orElse(20);
    String titleFormatPattern = "%-" + maxLength + "s";
    for (Information info : infos) {
      String title = String.format(titleFormatPattern, info.title());
      String versionWithColor;
      if (info.version().endsWith("-SNAPSHOT")) {
        versionWithColor = "\u001B[38;5;167mv" + info.version() + "\u001B[0m";
      } else {
        versionWithColor = "\u001B[38;5;157mv" + info.version() + "\u001B[0m";
      }

      System.out.printf("%s█%s  :: \u001B[38;5;184m%s\u001B[0m :: %s - %s%n",
          k,
          e,
          title,
          versionWithColor,
          info.description());
    }
    System.out.printf(
        "%s█▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄%s%n",
        k,
        e);
    System.out.println();
  }

  private static void printProjectInformation() {
    logger.atInfo().scene(LABZEN_TEXT).log("正在使用 'Labzen' 组件包...");

    List<Information> infos = Labzens.getComponentMetas()
                                     .values()
                                     .stream()
                                     .map(ComponentMeta::information)
                                     .sorted(Comparator.comparing(Information::title))
                                     .toList();
    for (Information info : infos) {
      logger.atInfo().scene(LABZEN_TEXT).log("  component {}:{} loaded", info.title(), info.version());
    }

    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    logger.atInfo().scene(LABZEN_TEXT).log("JVM启动输入参数：{}", String.join(" ", runtimeMXBean.getInputArguments()));
    logger.atInfo()
          .scene(LABZEN_TEXT)
          .log("JVM名称：{}, version {}", runtimeMXBean.getName(), runtimeMXBean.getSpecVersion());
  }

  private static void printSystemInformation() {

    String e = "\u001B[0m";
    String b = "\u001B[38;5;178m";
    String[] c = new String[]{"\u001B[38;5;107m", "\u001B[38;5;137m"};

    List<SystemInformation> systemInformationList = Labzens.allSystemInformation()
                                                           .stream()
                                                           .filter(si -> si.description() != null)
                                                           .toList();

    // 加 1 代表的是序号后边那个点
    int indexLength = String.valueOf(systemInformationList.size()).length() + 1;
    String indexFormatPattern = "%" + indexLength + "s";
    int catalogMaxLength = systemInformationList.stream().mapToInt(si -> si.catalog().length()).max().orElse(20);
    String catalogFormatPattern = "%" + catalogMaxLength + "s";
    int nameMaxLength = systemInformationList.stream().mapToInt(si -> si.name().length()).max().orElse(20);
    String nameFormatPattern = "%-" + nameMaxLength + "s";

    System.out.println(" === 检测到当前主机信息 ===");

    String latestCatalog = "";
    int ci = 0;
    for (int i = 0; i < systemInformationList.size(); i++) {
      SystemInformation info = systemInformationList.get(i);
      String currentCatalog = info.catalog();

      String indexString = String.format(indexFormatPattern, i + ".");
      String catalogString = String.format(catalogFormatPattern, currentCatalog);
      String nameString = String.format(nameFormatPattern, info.name());

      if (!latestCatalog.equals(currentCatalog)) {
        latestCatalog = currentCatalog;
        ci = ci ^ 1;
      }

      System.out.printf("%s♎ %s %s[ %s :: %s ]%s %s  >>>  %s%n",
          b,
          indexString,
          c[ci],
          catalogString,
          nameString,
          e,
          info.title(),
          info.description());
    }
    System.out.println();
  }
}
