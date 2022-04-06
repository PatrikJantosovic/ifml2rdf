package com.jantosovic.ifml.api;

import java.util.Collections;
import java.util.List;

public interface NamedElement {

  String getName();

  String getId();

  // no benefit in strongly typing this and then casting and resolving..
  default List<DataProperty> getDataProperties() {
    return Collections.emptyList();
  }

  void addDataProperty(DataProperty dataProperty);

  default List<ObjectProperty> getObjectProperties() {
    return Collections.emptyList();
  }

  void addObjectProperty(ObjectProperty objectProperty);

}
