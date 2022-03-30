package com.jantosovic.ifml.api;

public final class DataProperty {

  private final String name;
  private final String value;

  public DataProperty(String name, String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Value of field name.
   *
   * @return value of field name
   */
  public String getName() {
    return name;
  }

  /**
   * Value of field value.
   *
   * @return value of field value
   */
  public String getValue() {
    return value;
  }
}
