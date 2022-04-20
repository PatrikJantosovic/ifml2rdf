package com.jantosovic.ifml.api;

/**
 * Internal representation of data-property.
 * Read from XMI and transformed into OWLDataProperty.
 */
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

  @Override
  public String toString() {
    return "DataProperty{"
        + "name='" + name + '\''
        + ", value='" + value + '\''
        + '}';
  }
}
