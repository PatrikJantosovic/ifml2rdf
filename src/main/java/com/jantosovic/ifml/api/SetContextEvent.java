package com.jantosovic.ifml.api;

public class SetContextEvent extends ThrowingEvent {

  public SetContextEvent(String name, String id) {
    super(name, id);
  }
}
