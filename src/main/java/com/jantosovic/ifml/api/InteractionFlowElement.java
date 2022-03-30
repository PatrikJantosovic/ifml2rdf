package com.jantosovic.ifml.api;

public abstract class InteractionFlowElement implements NamedElement {

  private final String name;
  private final String id;

  public InteractionFlowElement(String name, String id) {
    this.name = name;
    this.id = id;
  }

  /**
   * Value of field name.
   *
   * @return value of field name
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Value of field id.
   *
   * @return value of field id
   */
  @Override
  public String getId() {
    return id;
  }
}
