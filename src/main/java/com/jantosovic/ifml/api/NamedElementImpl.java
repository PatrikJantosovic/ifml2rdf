package com.jantosovic.ifml.api;

import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * Implementation of IFML:NamedElement and its descendants.
 */
public final class NamedElementImpl implements NamedElement {

  private final String name;
  private final String id;
  private final OWLClass metamodelOwlClass;

  private final List<DataProperty> dataProperties;
  private final List<ObjectProperty> objectProperties;

  NamedElementImpl(String name, String id, OWLClass metamodelOwlClass) {
    this.name = name;
    this.id = id;
    this.metamodelOwlClass = metamodelOwlClass;
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
  public OWLClass getMetamodelOwlClass() {
    return metamodelOwlClass;
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
    return objectProperties;
  }

  @Override
  public void addObjectProperty(ObjectProperty objectProperty) {
    if (objectProperty!=null) {
      this.objectProperties.add(objectProperty);
    }
  }

  @Override
  public String toString() {
    return "NamedElementImpl{"
        + "name='" + name + '\''
        + ", id='" + id + '\''
        + ", metamodelOwlClass=" + metamodelOwlClass
        + ", dataProperties=" + dataProperties
        + ", objectProperties=" + objectProperties
        + '}';
  }
}
