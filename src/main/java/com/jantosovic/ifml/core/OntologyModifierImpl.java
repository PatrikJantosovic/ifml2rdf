package com.jantosovic.ifml.core;

import com.jantosovic.ifml.api.NamedElement;
import com.jantosovic.ifml.cmd.ApplicationConfiguration;
import java.nio.file.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.springframework.stereotype.Component;

public class OntologyModifierImpl implements OntologyModifier {

  private static final Logger LOG = LogManager.getLogger(OntologyModifierImpl.class);

  private final Path target;
  private final OWLOntologyManager manager;
  private final OWLDataFactory factory;
  private final OWLOntology ontology;
  private final IRI iri;
  private final IRI sourceIRI;

  public OntologyModifierImpl(Path targetPath, String targetIRI, ApplicationConfiguration configuration)
      throws OWLOntologyCreationException {
    this.target = targetPath;
    this.manager = OWLManager.createOWLOntologyManager();
    this.factory = manager.getOWLDataFactory();
    // create new and import ontology
    var sourceOntology = manager.loadOntologyFromOntologyDocument(configuration.getMetamodelPath());
    this.iri = IRI.create(targetIRI);
    this.sourceIRI = IRI.create(configuration.getMetamodelIri());
    // create our new ontology for individuals
    this.ontology = manager.createOntology(iri);
    // import ifml ontology
    OWLImportsDeclaration im = factory.getOWLImportsDeclaration(sourceOntology.getOntologyID().getOntologyIRI());
    manager.applyChange(new AddImport(ontology, im));
  }

  @Override
  public void addIndividual(NamedElement element) {
    var individualName = element.getName();
    var className = element.getClass().getSimpleName();
    var individualIRI = IRI.create(iri.toString() + '#' + individualName);
    LOG.debug("Individual IRI created as: {}", individualIRI);
    var individual = factory.getOWLNamedIndividual(individualIRI);
    var classIRI = IRI.create(sourceIRI.toString() + '#' + className);
    LOG.debug("Class IRI created as: {}", classIRI);
    var owlClass = factory.getOWLClass(classIRI);
    var classAssertionAxiom = factory.getOWLClassAssertionAxiom(owlClass, individual);
    LOG.debug("Adding Axiom {} to Ontology: {}", classAssertionAxiom, ontology);
    manager.addAxiom(ontology, classAssertionAxiom);
    LOG.debug("Successfully added individual to Ontology: {}", ontology);
  }

  @Override
  public void addDataProperty() {

  }

  @Override
  public void addObjectProperty() {

  }

  @Override
  public void close() throws OWLOntologyStorageException {
    manager.saveOntology(ontology, new FileDocumentTarget(target.toFile()));
  }
}
