package com.jantosovic.ifml.api;

import java.util.Collections;
import java.util.List;

/**
 * NamedElement is IFML element whose descendants can be transformed.
 */
public interface NamedElement {

  /**
   * Name of the IFML element.
   * Will result into NamedIndividual name.
   *
   * @return name of ifml element
   */
  String getName();

  /**
   * Unique identified of IFML element (GUID).
   *
   * @return id of ifml element
   */
  String getId();

  /**
   * Returns all known data-properties of this individual.
   *
   * @return all data properties of individual
   */
  default List<DataProperty> getDataProperties() {
    return Collections.emptyList();
  }

  /**
   * Add data-property value to the individual.
   *
   * @param dataProperty - data property representation
   */
  void addDataProperty(DataProperty dataProperty);

  /**
   * Returns all known object-properties of this individual.
   *
   * @return all object properties of individual
   */
  default List<ObjectProperty> getObjectProperties() {
    return Collections.emptyList();
  }

  /**
   * Add object-proeprty value to the individual.
   *
   * @param objectProperty - object property representation
   */
  void addObjectProperty(ObjectProperty objectProperty);

}
