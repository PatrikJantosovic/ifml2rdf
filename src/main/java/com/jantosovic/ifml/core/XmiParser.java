package com.jantosovic.ifml.core;

import com.jantosovic.ifml.api.NamedElement;
import com.jantosovic.ifml.api.ObjectProperty;
import java.util.Collection;
import java.util.List;

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

  /**
   * Get children for given element.
   * Verify against all IFML elements that child is IFML element.
   *
   * @param element - parent element
   * @param individuals - all known IFML elements
   * @return object-properties representing all parent-child relationships
   */
  List<ObjectProperty> getChildren(NamedElement element,
      Collection<? extends NamedElement> individuals);

  /**
   * Get interaction flow source and target value.
   *
   * @param element - xml element holding attributes with values
   * @param attrNm - attribute name of xml element that we want to read
   * @return source/target value
   */
  String getFlowValue(NamedElement element, String attrNm);

  /**
   * Get object-property representing binding relationship.
   *
   * @param individual
   * @param individuals
   * @return
   */
  Collection<ObjectProperty> getBindingObjectProperties(NamedElement individual,
      Collection<? extends NamedElement> individuals);

}
