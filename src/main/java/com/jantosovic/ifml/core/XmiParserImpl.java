package com.jantosovic.ifml.core;

import com.jantosovic.ifml.api.DataProperty;
import com.jantosovic.ifml.api.IFMLFactory;
import com.jantosovic.ifml.api.NamedElement;
import com.jantosovic.ifml.api.ObjectProperty;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Implementation of XMI file parser.
 */
public class XmiParserImpl implements XmiParser {

  private final Path path;
  private final IFMLFactory ifmlFactory;

  public XmiParserImpl(Path path, IFMLFactory ifmlFactory) {
    this.path = path;
    this.ifmlFactory = ifmlFactory;
  }

  private static final Logger LOG = LogManager.getLogger(XmiParserImpl.class);

  private static Document read(Path path) {
    try {
      var documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      var documentBuilder = documentBuilderFactory.newDocumentBuilder();
      var document = documentBuilder.parse(path.toFile());
      document.getDocumentElement().normalize();
      return document;
    } catch (ParserConfigurationException | IOException | SAXException e) {
      LOG.error("Failed reading XMI file: {}", path, e);
      System.exit(1);
      return null;
    }
  }

  private static String getId(Element element) {
    var attributes = element.getAttributes();
    for (int idx = 0; idx < attributes.getLength(); idx++) {
      if (attributes.item(idx).getLocalName().startsWith("base_")) {
        return attributes.item(idx).getNodeValue();
      }
    }
    throw new IllegalStateException("ID missing for given element " + element.getLocalName());
  }

  private static String getName(String id, Document doc) {
    var xPathfactory = XPathFactory.newInstance();
    var xpath = xPathfactory.newXPath();
    try {
      var x = "//*[@*[local-name()='id']='" + id + "']";
      var expr = xpath.compile(x);
      var nl = (Node) expr.evaluate(doc, XPathConstants.NODE);
      var namedAttr = nl.getAttributes().getNamedItem("name");
      if (namedAttr == null) {
        throw new IllegalArgumentException("Name attribute missing in element.");
      }
      return namedAttr.getTextContent();
    } catch (XPathExpressionException e) {
      throw new IllegalArgumentException("Failed to fetch name value for element.", e);
    }
  }

  private static void addAttributes(NamedNodeMap attrs, NamedElement object) {
    object.addDataProperty(new DataProperty("id", object.getId()));
    object.addDataProperty(new DataProperty("name", object.getName()));
    for (int idx = 0; idx < attrs.getLength(); idx++) {
      var name = attrs.item(idx).getLocalName();
      var value = attrs.item(idx).getNodeValue();
      if (name.startsWith("base_")) {
        // we use this one as ID and it is already resolved
        continue;
      }
      object.addDataProperty(new DataProperty(name, value));
    }
  }

  public List<ObjectProperty> getChildren(NamedElement element, Collection<? extends NamedElement> individuals) {
    var result = new ArrayList<ObjectProperty>(1);
    var doc = read(path);
    var xPathfactory = XPathFactory.newInstance();
    var xpath = xPathfactory.newXPath();
    try {
      var x = "//*[@*[local-name()='id']='" + element.getId() + "']";
      var expr = xpath.compile(x);
      var node = (Node) expr.evaluate(doc, XPathConstants.NODE);
      var children = node.getChildNodes();
      for (int idx = 0; idx < children.getLength(); idx++) {
        var child = children.item(idx);
        if (child.getNodeType() != Node.ELEMENT_NODE) {
          continue;
        }
        var childId = child.getAttributes().getNamedItem("xmi:id").getNodeValue();
        var childIndividual = individuals.stream()
            .filter(individual -> individual.getId().equals(childId)) //verfy that child is actually IFML
            .findFirst();
        if (childIndividual.isPresent()) {
          var childClassName = childIndividual.get().getClass().getSimpleName();
          var name = "has" + childClassName;
          var value = childIndividual.get().getName();
          LOG.debug("Found Possible ObjectProperty for Individual {}, being: {} : {}",
              element.getName(), name, value);
          result.add(new ObjectProperty(name, value, childClassName));
        }
      }
    } catch (XPathExpressionException e) {
      LOG.error("Failed while looking for parent individual from XMI document.", e);
      System.exit(1);
    }
    return result;
  }

  public ObjectProperty getParent(NamedElement element, Collection<? extends NamedElement> individuals) {
    var doc = read(path);
    var xPathfactory = XPathFactory.newInstance();
    var xpath = xPathfactory.newXPath();
    try {
      var x = "//*[@*[local-name()='id']='" + element.getId() + "']";
      var expr = xpath.compile(x);
      var node = (Node) expr.evaluate(doc, XPathConstants.NODE);
      var parentNode = node.getParentNode();
      var parentId = parentNode.getAttributes().getNamedItem("xmi:id").getNodeValue();
      var parent = individuals.stream()
          .filter(individual -> individual.getId().equals(parentId)) //verfy that parent is actually IFML
          .findFirst();
      if (parent.isPresent()) {
        var parentClassName = parent.get().getClass().getSimpleName();
        var name = "has" + parentClassName;
        var value = parent.get().getName();
        // we will evaluate it against ifml owl metamodel before inserting
        LOG.debug("Found Possible ObjectProperty for Individual {}, being: {} : {}",
            element.getName(), name, value);
        return new ObjectProperty(name, value, parentClassName);
      }
    } catch (XPathExpressionException e) {
      LOG.error("Failed while looking for parent individual from XMI document.", e);
      System.exit(1);
    }
    return null;
  }

  public String getFlowValue(NamedElement element, String attrNm) {
    var objectProperties = new ArrayList<ObjectProperty>(2);
    var doc = read(path);
    var xPathfactory = XPathFactory.newInstance();
    var xpath = xPathfactory.newXPath();
    try {
      var x = "//*[@*[local-name()='id']='" + element.getId() + "']";
      var expr = xpath.compile(x);
      var node = (Node) expr.evaluate(doc, XPathConstants.NODE);
      var attrs = node.getAttributes();
      var client = attrs.getNamedItem(attrNm).getNodeValue();
      return getName(client, doc);
    } catch (XPathExpressionException e) {
      LOG.error("Failed while looking for dependency association from XMI document.", e);
      System.exit(1);
    }
    return null;
  }

  public Collection<ObjectProperty> getBindingObjectProperties(NamedElement individual, Collection<? extends NamedElement> individuals) {
    var result = new ArrayList<ObjectProperty>(5);
    var doc = read(path);
    var xpath = XPathFactory.newInstance().newXPath();
    try {
      var expression = xpath.compile("//*[starts-with(name(), 'thecustomprofile')]");
      var nodes = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
      for (int idx = 0; idx < nodes.getLength(); idx++) {
        var node = nodes.item(idx);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          var element = (Element) node;
          var id = getId(element);
          if (!id.equals(individual.getId())) {
            // this belongs to another individual..
            continue;
          }
          var name = element.getLocalName();
          var value = element.getAttributes().getNamedItem(name).getNodeValue();
          var targetClassName = individuals.stream().filter(i -> value.equals(i.getName())).findFirst();
          result.add(new ObjectProperty(name, value, targetClassName.orElseThrow().getClass().getSimpleName()));
        }
      }
    } catch (XPathExpressionException e) {
      LOG.error("Failed while parsing binding object-property from XMI document.", e);
      System.exit(1);
    }
    return result;
  }

  @Override
  public Collection<NamedElement> getIndividuals() {
    var result = new ArrayList<NamedElement>(5);
    var doc = read(path);
    var xpath = XPathFactory.newInstance().newXPath();
    try {
      var expression = xpath.compile("//*[starts-with(name(), 'IFML')]");
      var nodes = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
      for (int idx = 0; idx < nodes.getLength(); idx++) {
        var node = nodes.item(idx);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          var element = (Element) node;
          var id = getId(element);
          var name = getName(id, doc);
          var attrs = element.getAttributes();
          LOG.debug("IFML element found: {}, with name: {} and id: {}", element.getTagName(), name, id);
          var object = ifmlFactory.createNamed(name, id, element);
          addAttributes(attrs, object);
          result.add(object);
        }
      }
    } catch (XPathExpressionException e) {
      LOG.error("Failed while parsing individuals from XMI document.", e);
      System.exit(1);
    }
    return result;
  }

  @Override
  public String toString() {
    return "XmiParserImpl{"
        + "path=" + path
        + ", ifmlFactory=" + ifmlFactory
        + '}';
  }
}
