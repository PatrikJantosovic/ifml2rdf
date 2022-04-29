package com.jantosovic.ifml.core;

import com.jantosovic.ifml.api.NamedElement;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Interface for Ontology Modifier.
 * Extends Autocloseable to close File resource.
 */
public interface OntologyModifier extends AutoCloseable {

  /**
   * Return ontology individual by given name.
   * If individual is not present throws @IllegalStateException.
   *
   * @param name of individual
   * @return instance of individual
   */
  OWLNamedIndividual getIndividualByName(String name);

  /**
   * Return object-property by given name from metamodel ontology.
   * If object property is not present throws @IllegalStateException.
   *
   * @param name of object property
   * @return owl object property
   */
  OWLObjectProperty getObjectPropertyByName(String name);

  /**
   * Creates owl named individual with given name.
   *
   * @param name of individual to be created
   * @return instance of individual
   */
  OWLNamedIndividual createIndividual(String name);

  /**
   * Creates an instance of NamedIndividual for provided IFML element.
   *
   * @param element that should be converted to NamedIndividual
   */
  void addIndividual(NamedElement element);

  /**
   * Add DataProperty axioms to the Ontology.
   *
   * @param element with the data-properties
   */
  void addDataProperties(NamedElement element);

  /**
   * Add ObjectProperty axioms to the Ontology.
   *
   * @param objectProperty that should be added
   * @param sourceIndividual subject of the object property
   * @param targetName object of the object-property
   */
  void addObjectProperty(OWLObjectProperty objectProperty,
      OWLNamedIndividual sourceIndividual, String targetName);

  /**
   * Adds ObjectProperty axiom to the Ontology.
   *
   * @param element that should have the object-property
   */
  void addObjectProperties(NamedElement element);

}
