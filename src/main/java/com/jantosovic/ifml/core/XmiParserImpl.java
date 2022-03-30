package com.jantosovic.ifml.core;

import com.jantosovic.ifml.api.IFMLFactory;
import com.jantosovic.ifml.api.NamedElement;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

      // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
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
    return null;
  }

  private static String getName(String id, Path pathToDoc) {
    var doc = read(pathToDoc);
    var xPathfactory = XPathFactory.newInstance();
    var xpath = xPathfactory.newXPath();
    try {
      var x = "//*[@*[local-name()='id']='" + id + "']";
      var expr = xpath.compile(x);
      var nl = (Node) expr.evaluate(doc, XPathConstants.NODE);
      return nl.getAttributes().getNamedItem("name").getTextContent();
    } catch (XPathExpressionException e) {
      throw new IllegalArgumentException("Failed to fetch name value for element.", e);
    }
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
          var name = getName(id, path);
          LOG.debug("IFML element found: {}, with name: {} and id: {}", element.getTagName(), name, id);
          var object = ifmlFactory.createNamed(name, id, element);
          result.add(object);
        }
      }
    } catch (XPathExpressionException e) {
      LOG.error("Failed while parsing individuals from XMI document.", e);
      System.exit(1);
    }
    return result;
  }

}
