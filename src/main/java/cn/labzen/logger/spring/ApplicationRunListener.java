package cn.labzen.logger.spring;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;
import java.util.List;

public class ApplicationRunListener implements SpringApplicationRunListener {

  //private final SpringApplication application;
  //private final String[] args;

  //public ApplicationRunListener(SpringApplication application, String[] args) {
  //  this.application = application;
  //  this.args = args;
  //}

  @Override
  public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
    String[] activeProfiles = environment.getActiveProfiles();
    if (activeProfiles.length == 0) {
      Profiles.setCurrentProfiles(List.of(Profiles.DEFAULT));
    } else {
      Profiles.setCurrentProfiles(Arrays.asList(activeProfiles));
    }
  }
}
