package com.jantosovic.ifml.core;

import com.jantosovic.ifml.api.NamedElement;

public interface OntologyModifier extends AutoCloseable {

  void addIndividual(NamedElement element);

  void addDataProperty();

  void addObjectProperty();

}
