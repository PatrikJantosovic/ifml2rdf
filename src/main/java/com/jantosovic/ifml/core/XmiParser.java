package com.jantosovic.ifml.core;

import com.jantosovic.ifml.api.InteractionFlowElement;
import com.jantosovic.ifml.api.NamedElement;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public interface XmiParser {

  Collection<NamedElement> getIndividuals();

}
