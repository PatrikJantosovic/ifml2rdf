package com.jantosovic.ifml.core;

import com.jantosovic.ifml.api.NamedElement;

/**
 * Interface for Ontology Modifier.
 * Extends Autocloseable to close File resource.
 */
public interface OntologyModifier extends AutoCloseable {

  /**
   * Creates an instance of NamedIndividual for provided element.
   *
   * @param element that should be converted to NamedIndividual
   */
  void addIndividual(NamedElement element);

  /**
   * Adds DataProperty axiom to the Ontology.
   *
   * @param element that should have the data-property
   */
  void addDataProperties(NamedElement element);

  /**
   * Adds ObjectProperty axiom to the Ontology.
   *
   * @param element that should have the object-property
   */
  void addObjectProperties(NamedElement element);

}
