# IFML2RDF

Simple transformation application used for converting IFML models into ontologies.

## Structure
The application is divided into multiple packages. **API** package contains classes describing IFML elements, a class representing OWL data property structure, and a class representing OWL object property structure.
The **cmd** package provides an entry point to the application through the **picocli** command-line interface and also implements spring configuration loading. Most of the logic resides in **core** package, with an XMI parser class and OWL modifier class, which are classes implementing the logic of reading information from IFML models in XMI format and writing them to the resulting files in RDF/OWL format.

## Input
Application properties file requires path to IFML metamodel ontology and the IRI of the ontology.
The sample file is provided in releases section.

Command line parameters:

*--path=""*  - path to XMI file

*--target=""* - output file path

*--iri=""* - target ontology iri

## Output
IFML model transformed to ontology in RDF/OWL format.
