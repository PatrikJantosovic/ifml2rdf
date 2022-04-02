package com.jantosovic.ifml.api;

import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

public final class IFMLFactory {

  private static final Logger LOG = LogManager.getLogger(IFMLFactory.class);

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
      LOG.error("Failed to initialize object for {}", element.getLocalName(), e);
      System.exit(1);
      return null;
    }
  }

}
