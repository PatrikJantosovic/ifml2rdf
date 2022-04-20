package com.jantosovic.ifml.api;

/**
 * Internal representation of object-property.
 * Read from XMI file, transformed into OWLObjectProperty.
 */
public final class ObjectProperty {

  private final String name;
  private final String value;
  private final String targetClassName;

  public ObjectProperty(String name, String value, String targetClassName) {
    this.name = name;
    this.value = value;
    this.targetClassName = targetClassName;
  }

  /**
   * Value of field value.
   *
   * @return value of field value
   */
  public String getValue() {
    return value;
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
   * Value of field targetClassName.
   *
   * @return value of field targetClassName
   */
  public String getTargetClassName() {
    return targetClassName;
  }

  @Override
  public String toString() {
    return "ObjectProperty{"
        + "name='" + name + '\''
        + ", value='" + value + '\''
        + ", targetClassName='" + targetClassName + '\''
        + '}';
  }
}
