package cn.labzen.logger;

import org.springframework.boot.SpringApplication;

public class SpringBootTest {

  public static void main(String[] args) {
    Loggers.enhance();
    SpringApplication.run(SpringBootTest.class, args);
  }
}
