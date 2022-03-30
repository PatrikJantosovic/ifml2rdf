package com.jantosovic.ifml.cmd;

import java.io.File;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
public class ApplicationConfiguration {

  @Value( "${metamodel.iri}" )
  private String metamodelIri;

  @Value( "${metamodel.path}" )
  private String metamodelPath;

  /**
   * Value of field metamodelIri.
   *
   * @return value of field metamodelIri
   */
  public String getMetamodelIri() {
    return metamodelIri;
  }

  /**
   * Value of field metamodelPath.
   *
   * @return value of field metamodelPath
   */
  public File getMetamodelPath() {
    return Path.of(metamodelPath).toFile();
  }
}
