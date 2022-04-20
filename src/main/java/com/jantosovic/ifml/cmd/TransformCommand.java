package com.jantosovic.ifml.cmd;

import com.jantosovic.ifml.api.IFMLFactory;
import com.jantosovic.ifml.api.InteractionFlow;
import com.jantosovic.ifml.core.OntologyModifierImpl;
import com.jantosovic.ifml.core.XmiParserImpl;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Component
@Command(
    name = "transform",
    description = "Read input IFML file in XMI format and create individuals in IFML Ontology."
)
public class TransformCommand implements Callable<Integer> {

  private static final Logger LOG = LogManager.getLogger(TransformCommand.class);

  @Autowired
  private ApplicationConfiguration configuration;

  @Option(names = {"-p", "--path"}, defaultValue = "",
      description = "Path to input file to be parsed.")
  private String path;

  @Option(names = {"-t", "--target"}, defaultValue = "",
      description = "Path where target file shall be created.")
  private String target;

  @Option(names = {"-i", "--iri"}, defaultValue = "",
      description = "IRI of a newly created file.")
  private String iri;

  @Override
  public Integer call() {
    LOG.info("Reading from file: {}", path);
    try (var modifier = new OntologyModifierImpl(Path.of(target), iri, configuration)) {
      var source = new XmiParserImpl(Path.of(path), new IFMLFactory());
      // read individuals and their attributes from XMI
      var individuals = source.getIndividuals();
      // add individuals and data-properties to ontology
      individuals.forEach(modifier::addIndividual);
      individuals.forEach(modifier::addDataProperties);
      // read parent->child object-properties
      individuals.forEach(individual -> {
        source.getChildren(individual, individuals).forEach(individual::addObjectProperty);
      });
      // add child<->parent object-properties to ontology
      individuals.forEach(modifier::addObjectProperties);
      // read and add binding object-properties
      individuals.forEach(individual -> {
        source.getBindingObjectProperties(individual, individuals)
            .forEach(objectProperty ->
                modifier.addObjectProperty(
                    modifier.getObjectPropertyByName(objectProperty.getName()),
                    modifier.getIndividualByName(individual.getName()),
                    objectProperty.getValue()
            ));
      });
      // read and add flow object-properties
      individuals.forEach(individual -> {
        if (individual instanceof InteractionFlow) {
          modifier.addObjectProperty(
              modifier.getObjectPropertyByName("hasSourceInteractionFlowElement"),
              modifier.getIndividualByName(individual.getName()),
              source.getFlowValue(individual, "client"));
          modifier.addObjectProperty(
              modifier.getObjectPropertyByName("hasTargetInteractionFlowElement"),
              modifier.getIndividualByName(individual.getName()),
              source.getFlowValue(individual, "supplier"));
        }
      });
    } catch (OWLOntologyStorageException | OWLOntologyCreationException e) {
      LOG.error("Failed to transform file ", e);
    }
    LOG.info("Successfully transformed file {} to Ontology {}.", path, iri);
    return 0;
  }

  @Override
  public String toString() {
    return "TransformCommand{"
        + "configuration=" + configuration
        + ", path='" + path + '\''
        + ", target='" + target + '\''
        + ", iri='" + iri + '\''
        + '}';
  }
}
