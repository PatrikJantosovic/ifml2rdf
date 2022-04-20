package com.jantosovic.ifml.core;

import com.jantosovic.ifml.api.NamedElement;
import com.jantosovic.ifml.api.ObjectProperty;
import com.jantosovic.ifml.cmd.ApplicationConfiguration;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentTarget;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public final class OntologyModifierImpl implements OntologyModifier {

  private static final Logger LOG = LogManager.getLogger(OntologyModifierImpl.class);

  private final Path target;
  private final OWLOntologyManager manager;
  private final OWLDataFactory factory;
  private final OWLOntology metamodelOntology;
  private final OWLOntology ontology;
  private final IRI iri;
  private final IRI metamodelIRI;

  public OntologyModifierImpl(Path targetPath, String targetIRI, ApplicationConfiguration configuration)
      throws OWLOntologyCreationException {
    this.target = targetPath;
    this.manager = OWLManager.createOWLOntologyManager();
    this.factory = manager.getOWLDataFactory();
    // create new and import ontology
    this.metamodelOntology = manager.loadOntologyFromOntologyDocument(configuration.getMetamodelPath());
    this.iri = IRI.create(targetIRI);
    this.metamodelIRI = IRI.create(configuration.getMetamodelIri());
    // create our new ontology for individuals
    this.ontology = manager.createOntology(iri);
    // import ifml ontology
    var importDeclaration = factory.getOWLImportsDeclaration(metamodelOntology.getOntologyID().getOntologyIRI());
    manager.applyChange(new AddImport(ontology, importDeclaration));
  }

  public OWLNamedIndividual getIndividualByName(String name) {
    var individulIri = IRI.create(iri.toString() + '#' + name);
    if (ontology.containsIndividualInSignature(individulIri)) {
      return factory.getOWLNamedIndividual(individulIri);
    }
    throw new IllegalStateException("Individual not found for name: " + name);
  }

  public OWLObjectProperty getObjectPropertyByName(String name) {
    var objectPropertyIRI = IRI.create(metamodelIRI.toString() + '#' + name);
    if (metamodelOntology.containsObjectPropertyInSignature(objectPropertyIRI)) {
      return factory.getOWLObjectProperty(objectPropertyIRI);
    }
    throw new IllegalStateException("Object property not found for name: " + name);
  }

  private OWLClass getMetamodelClassByName(String name) {
    var classIRI = IRI.create(metamodelIRI.toString() + '#' + name);
    if (metamodelOntology.containsClassInSignature(classIRI)) {
      return factory.getOWLClass(classIRI);
    }
    throw new IllegalStateException("Class not found for name: " + name);
  }

  public OWLNamedIndividual createIndividual(String name) {
    var individulIri = IRI.create(iri.toString() + '#' + name);
    return factory.getOWLNamedIndividual(individulIri);
  }

  private Set<OWLClass> getSuperClasses(OWLClass owlClass) {
    var reasoner = new Reasoner.ReasonerFactory().createReasoner(metamodelOntology);
    return reasoner.getSuperClasses(owlClass, false).getFlattened();
  }

  @Override
  public void addIndividual(NamedElement element) {
    var individual = createIndividual(element.getName());
    var owlClass = getMetamodelClassByName(element.getClass().getSimpleName());
    var classAssertionAxiom = factory.getOWLClassAssertionAxiom(owlClass, individual);
    LOG.debug("Adding Axiom {} to Ontology: {}", classAssertionAxiom, ontology);
    manager.addAxiom(ontology, classAssertionAxiom);
    LOG.info("Successfully added individual to Ontology: {}", ontology);
  }

  @Override
  public void addDataProperties(NamedElement element) {
    var individual = getIndividualByName(element.getName());
    element.getDataProperties().forEach(dataProperty -> {
      var owlDataProperty = factory.getOWLDataProperty(IRI.create(metamodelIRI.toString() + '#' + dataProperty.getName()));
      var dataPropertyAssertion = factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, individual, dataProperty.getValue());
      LOG.info("Modifying {} with data-property {} and value {}.", individual, dataProperty.getName(), dataProperty.getValue());
      manager.addAxiom(ontology, dataPropertyAssertion);
    });
  }

  private Set<OWLObjectProperty> getObjectPropertiesForDomain(OWLClass owlClass) {
    var classHierarchy = new HashSet<OWLClass>(1);
    classHierarchy.add(owlClass);
    classHierarchy.addAll(getSuperClasses(owlClass));
    var objectProperties = new HashSet<OWLObjectProperty>();
    var knownObjectProperties = metamodelOntology.getObjectPropertiesInSignature();
    classHierarchy.forEach(clazz -> {
      knownObjectProperties.forEach(objectProperty -> {
        var domains = new HashSet<OWLClass>();
        objectProperty.getDomains(metamodelOntology).forEach(classExpression ->
            domains.addAll(
                classExpression
                    .asDisjunctSet()
                    .stream()
                    .map(OWLClassExpression::asOWLClass)
                    .collect(Collectors.toUnmodifiableSet())));
        if (domains.contains(clazz)) {
          objectProperties.add(objectProperty);
        }
      });
    });
   return objectProperties;
  }

  private Set<OWLObjectProperty> traverseObjectPropertiesForRange(Set<OWLClass> owlClasses) {
    var objectProperties = new HashSet<OWLObjectProperty>();
    var knownObjectProperties = metamodelOntology.getObjectPropertiesInSignature();
    owlClasses.forEach(clazz -> {
      knownObjectProperties.forEach(objectProperty -> {
        var domains = new HashSet<OWLClass>();
        objectProperty.getRanges(metamodelOntology).forEach(classExpression ->
            domains.addAll(
                classExpression
                    .asDisjunctSet()
                    .stream()
                    .map(OWLClassExpression::asOWLClass)
                    .collect(Collectors.toUnmodifiableSet())));
        if (domains.contains(clazz)) {
          objectProperties.add(objectProperty);
        }
      });
    });
    return objectProperties;
  }

  private Set<OWLObjectProperty> getObjectPropertiesForRange(OWLClass owlClass) {
    var classHierarchy = new HashSet<OWLClass>(1);
    classHierarchy.add(owlClass);
    classHierarchy.addAll(getSuperClasses(owlClass));
    return traverseObjectPropertiesForRange(classHierarchy);
  }

  public void addObjectProperty(OWLObjectProperty objectProperty,
      OWLNamedIndividual sourceIndividual, String targetName) {
    var targetIndividual = factory.getOWLNamedIndividual(
          IRI.create(iri.toString() + '#' + targetName));
    var objectPropertyAssertion = factory
          .getOWLObjectPropertyAssertionAxiom(objectProperty, sourceIndividual, targetIndividual);
    LOG.info("Modifying {} with object-property {} and value {}.", sourceIndividual,
          objectProperty, targetIndividual);
    manager.addAxiom(ontology, objectPropertyAssertion);
  }

  private OWLObjectProperty inferOwlObjectProperty(ObjectProperty obj, String sourceClassName) {
    var sourceClass = getMetamodelClassByName(sourceClassName);
    var targetClass = getMetamodelClassByName(obj.getTargetClassName());
    var domainObjectProperties = getObjectPropertiesForDomain(sourceClass);
    var rangeObjectProperties = getObjectPropertiesForRange(targetClass);
    domainObjectProperties.retainAll(rangeObjectProperties);
    // when we have multiple results lets check if one of them is not exact match to the target class
    if (domainObjectProperties.size() > 1) {
      var possibleResults = new HashSet<>(domainObjectProperties);
      var exactRangeMatch = traverseObjectPropertiesForRange(Set.of(targetClass));
      possibleResults.retainAll(exactRangeMatch);
      return possibleResults.stream().findFirst().orElseGet(
          () -> domainObjectProperties.stream().findFirst().orElse(null));
    }
    return domainObjectProperties.stream()
        .findFirst()
        .orElse(null);
  }

  @Override
  public void addObjectProperties(NamedElement element) {
    var individual = getIndividualByName(element.getName());
    element.getObjectProperties().forEach(objectProperty -> {
      var owlObjectProperty = inferOwlObjectProperty(objectProperty, element.getClass().getSimpleName());
      if (owlObjectProperty != null) {
        addObjectProperty(owlObjectProperty, individual, objectProperty.getValue());
      }
    });
  }

  @Override
  public void close() throws OWLOntologyStorageException {
    manager.saveOntology(ontology, new FileDocumentTarget(target.toFile()));
  }

  @Override
  public String toString() {
    return "OntologyModifierImpl{"
        + "target=" + target
        + ", manager=" + manager
        + ", factory=" + factory
        + ", metamodelOntology=" + metamodelOntology
        + ", ontology=" + ontology
        + ", iri=" + iri
        + ", metamodelIRI=" + metamodelIRI
        + '}';
  }

}
