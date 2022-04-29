package com.jantosovic.ifml.api;

import com.jantosovic.ifml.core.OntologyModifier;
import java.lang.reflect.InvocationTargetException;
import org.semanticweb.owlapi.model.OWLClass;
import org.w3c.dom.Element;

/**
 * Factory method for IFML elements.
 * Resolves XML element name and creates appropriate instance.
 */
public final class IFMLFactory {

  private final OntologyModifier modifier;

  public IFMLFactory(OntologyModifier modifier) {
    this.modifier = modifier;
  }

  /**
   * Creates instance of named element based on XML element supplied.
   *
   * @param name - value of attribute name
   * @param id - value of attribute id
   * @param xmlElement - element
   * @return instance of named element
   */
  public NamedElementImpl createNamedElement(String name, String id, Element xmlElement) {
    var ifmlElementName = xmlElement.getLocalName();
    var metamodelOwlClass = modifier.getMetamodelClassByName(ifmlElementName);
    return new NamedElementImpl(name, id, metamodelOwlClass);
  }

  @Override
  public String toString() {
    return "IFMLFactory{"
        + "modifier=" + modifier
        + '}';
  }
}
