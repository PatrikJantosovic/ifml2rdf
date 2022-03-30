package com.jantosovic.ifml.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class InteractionFlowElement implements NamedElement {

  private final String name;
  private final String id;

  private final java.util.List<DataProperty> dataPropertyList;

  public InteractionFlowElement(String name, String id) {
    this.name = name;
    this.id = id;
    dataPropertyList = new ArrayList<>();
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

  @Override
  public List<DataProperty> getDataProperties() {
    return dataPropertyList;
  }

  public void addDataProperty(DataProperty dataProperty) {
    this.dataPropertyList.add(dataProperty);
  }

}
