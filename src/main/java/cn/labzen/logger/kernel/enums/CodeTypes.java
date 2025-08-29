package cn.labzen.logger.kernel.enums;

public enum CodeTypes {
  JAVA("Java"), JS("JS  "), XML("XML "), JSON("JSON"), YAML("YAML");

  private final String text;

  public String getText() {
    return text;
  }

  CodeTypes(String text) {
    this.text = text;
  }
}
