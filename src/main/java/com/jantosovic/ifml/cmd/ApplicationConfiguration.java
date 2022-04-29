package com.jantosovic.ifml.cmd;

import java.io.File;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

  @Value( "${metamodel.iri}" )
  private String metamodelIri;

  @Value( "${metamodel.path}" )
  private String metamodelPath;

  @Value( "${model.syntax}" )
  private String syntax;

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

  /**
   * Value of field syntax.
   *
   * @return value of field syntax
   */
  public String getSyntax() {
    return syntax;
  }

  @Override
  public String toString() {
    return "ApplicationConfiguration{"
        + "metamodelIri='" + metamodelIri + '\''
        + ", metamodelPath='" + metamodelPath + '\''
        + ", syntax='" + syntax + '\''
        + '}';
  }
}
