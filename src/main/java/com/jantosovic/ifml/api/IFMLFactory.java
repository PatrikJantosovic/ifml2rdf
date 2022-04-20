package com.jantosovic.ifml.api;

import java.lang.reflect.InvocationTargetException;
import org.w3c.dom.Element;

/**
 * Factory method for IFML elements.
 * Resolves XML element name and creates appropriate instance.
 */
public final class IFMLFactory {

  /**
   * Creates instance of named element based on XML element supplied.
   *
   * @param name - value of attribute name
   * @param id - value of attribute id
   * @param element - element
   * @return instance of named element
   */
  public NamedElement createNamed(String name, String id, Element element) {
    try {
      var parameterTypes = new Class[2];
      parameterTypes[0] = String.class;
      parameterTypes[1] = String.class;
      var args = new Object[2];
      args[0] = name;
      args[1] = id;
      var object = Class.forName(getClass().getPackageName() + '.' + element.getLocalName())
          .getConstructor(parameterTypes)
          .newInstance(args);
      return (NamedElement) object;
    } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
        ClassNotFoundException | IllegalAccessException e) {
      throw new IllegalArgumentException("Failed to initialize object for " + element.getLocalName(), e);
    }
  }

}
