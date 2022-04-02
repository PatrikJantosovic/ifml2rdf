package com.jantosovic.ifml.core;

import com.jantosovic.ifml.api.NamedElement;
import java.util.Collection;

/**
 * Interface for XMI file parser.
 */
public interface XmiParser {

  /**
   * Read all IFML elements from source XMI file.
   * IFML elements are recognized by IFML: namespace, and are expected to have an identifier
   * and a name as these are used to create instances of NamedIndividuals in OWL.
   *
   * @return collection of NamedElements
   */
  Collection<NamedElement> getIndividuals();

}
