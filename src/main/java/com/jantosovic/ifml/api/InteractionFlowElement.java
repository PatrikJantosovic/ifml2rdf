package com.jantosovic.ifml.api;

import java.util.ArrayList;
import java.util.List;

public abstract class InteractionFlowElement implements NamedElement {

  private final String name;
  private final String id;

  private final java.util.List<DataProperty> dataProperties;
  private final java.util.List<ObjectProperty> objectProperties;

  public InteractionFlowElement(String name, String id) {
    this.name = name;
    this.id = id;
    dataProperties = new ArrayList<>();
    objectProperties = new ArrayList<>();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public List<DataProperty> getDataProperties() {
    return dataProperties;
  }

  @Override
  public void addDataProperty(DataProperty dataProperty) {
    if (dataProperty!=null) {
      this.dataProperties.add(dataProperty);
    }
  }

  @Override
  public List<ObjectProperty> getObjectProperties() {
    return this.objectProperties;
  }

  @Override
  public void addObjectProperty(ObjectProperty objectProperty) {
    if (objectProperty!=null) {
      this.objectProperties.add(objectProperty);
    }
  }

  @Override
  public String toString() {
    return "InteractionFlowElement{"
        + "name='" + name + '\''
        + ", id='" + id + '\''
        + ", dataProperties=" + dataProperties
        + ", objectProperties=" + objectProperties
        + '}';
  }
}
