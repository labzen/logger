package cn.labzen.logger.spring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Profiles {

  public static final String DEFAULT = "";
  public static final String DEV = "dev";
  public static final String PROD = "prod";
  public static final String TEST = "test";
  public static final String UAT = "uat";
  public static final String DEV_FULL = "development";
  public static final String PROD_FULL = "production";

  private static final List<String> currentProfiles = new ArrayList<>();

  private Profiles() {
  }

  public static List<String> currentProfiles() {
    return Collections.unmodifiableList(currentProfiles);
  }

  static void setCurrentProfiles(List<String> profiles) {
    currentProfiles.clear();
    currentProfiles.addAll(profiles);
  }
}
