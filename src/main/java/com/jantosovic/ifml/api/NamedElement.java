package com.jantosovic.ifml.api;

import java.util.Collections;
import java.util.List;

public interface NamedElement {

  String getName();

  String getId();

  default List<DataProperty> getDataProperties() {
    return Collections.emptyList();
  }

  void addDataProperty(DataProperty dataProperty);

}
