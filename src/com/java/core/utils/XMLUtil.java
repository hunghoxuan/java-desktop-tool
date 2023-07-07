package com.java.core.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.java.core.components.MyDialog;
import com.java.core.components.MyTreeMap;
import com.java.core.logs.LogManager;
import com.java.core.settings.Settings;

public class XMLUtil {
    public static Node appendXmlNode(Document doc, String xmlTag) {
        return appendXmlNode(doc, xmlTag, null);
    }

    public static Node appendXmlNode(Document doc, String xmlTag, String xmlValue) {
        if (doc == null) {
            doc = XMLUtil.createXmlDocument();
        }
        Element neuesElement = doc.createElement(xmlTag);
        if (xmlValue != null)
            neuesElement.appendChild(doc.createTextNode(xmlValue));
        XMLUtil.getXMLNodeByTagName(doc, "ROOT").appendChild(neuesElement);
        return neuesElement;
    }

    public static void storeIniSettingsListValues(String iniFile, List<String> values, String listXmlTag,
            String elementXmlTag) {
        if (true)
            return;
        Document doc = XMLUtil.getXmlDocument(iniFile);
        Node fileLocatorNode = XMLUtil.getXMLNodeByTagName(doc, listXmlTag);
        if (fileLocatorNode == null) {
            fileLocatorNode = XMLUtil.appendXmlNode(doc, listXmlTag);
        }

        boolean tagUpdated = false;
        for (int t = 0; t < fileLocatorNode.getChildNodes().getLength(); t++) {
            boolean needRemove = true;
            for (String fileLocator : values) {
                if (fileLocator.equals(fileLocatorNode.getChildNodes().item(t).getTextContent()))
                    needRemove = false;
            }
            if (needRemove)
                fileLocatorNode = fileLocatorNode.removeChild(fileLocatorNode.getChildNodes().item(t));
        }

        for (String fileLocator : values) {// check if the file locator list contains file locator which are
                                           // not stored in the ini setting
            boolean fileLocatorIsStored = false;
            for (int t = 0; t < fileLocatorNode.getChildNodes().getLength(); t++) {
                if (fileLocator.equals(fileLocatorNode.getChildNodes().item(t).getTextContent()))
                    fileLocatorIsStored = true;
            }
            if (!fileLocatorIsStored) {
                for (int tt = 0; tt < fileLocatorNode.getChildNodes().getLength(); tt++) {
                    if (fileLocatorNode.getChildNodes().item(tt).getNodeType() == 1)
                        doc.renameNode(fileLocatorNode.getChildNodes().item(tt), null,
                                elementXmlTag + String.valueOf(Integer.valueOf(
                                        fileLocatorNode.getChildNodes().item(tt).getNodeName()
                                                .substring(elementXmlTag.length()))
                                        + 1));
                }
                Stack<Node> nodeStapel = new Stack<Node>();
                int childrenCount = fileLocatorNode.getChildNodes().getLength();
                for (int ttt = 0; ttt < childrenCount; ttt++) {
                    if (fileLocatorNode.getChildNodes().item(fileLocatorNode.getChildNodes().getLength() - 1)
                            .getNodeType() == 1)//
                        nodeStapel.push(
                                fileLocatorNode.getChildNodes().item(fileLocatorNode.getChildNodes().getLength() - 1));
                    fileLocatorNode.removeChild(
                            fileLocatorNode.getChildNodes().item(fileLocatorNode.getChildNodes().getLength() - 1));
                }
                Element neuesElement = doc.createElement(elementXmlTag + String.valueOf(0));

                neuesElement
                        .appendChild(doc
                                .createTextNode(Settings.getIniSettingFolderValue(elementXmlTag, fileLocator, true)));
                nodeStapel.push(neuesElement);
                tagUpdated = true;
                childrenCount = 1;

                while (!nodeStapel.isEmpty() && childrenCount <= 10) {
                    try {
                        fileLocatorNode.appendChild(nodeStapel.pop());
                        childrenCount++;
                    } catch (DOMException ex) {
                        LogManager.getLogger().error(ex);
                    }
                }
            }
        }
        if (tagUpdated) {
            XMLUtil.storeXmlDocToFile(iniFile, doc);
            Settings.loadSettings();
        }
    }

    public static void storeXmlDocToFile(String iniFile, Document doc) {

        try {
            File f = new File(iniFile);
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException tce) {
            MyDialog.showException(tce, "Error thrown during parsing of the XML data");

        } catch (TransformerException te) {
            MyDialog.showException(te, "Error thrown during parsing of the XML data");
        } catch (Exception e) {
            MyDialog.showException(e, "Error thrown during laoding the XML file " + iniFile);
        }

    }

    public static List<Map<String, String>> loadInitSettingsListTags(String iniFile, String listXmlTag) {
        Document doc = XMLUtil.getXmlDocument(iniFile);

        List<Map<String, String>> xmlExtrakt = XMLUtil.loadXmlTags(doc, listXmlTag);
        return xmlExtrakt;
    }

    public static List<String> loadInitSettingsListValues(String listXmlTag, String elementXmlTag,
            String excludedValue) {

        List<String> storedFileLocators = new LinkedList<String>();
        List<Map<String, String>> fileLocators = loadInitSettingsListTags(Settings.getIniFilePath(), listXmlTag);
        if (fileLocators.size() > 0) {
            for (int t = 0; t < fileLocators.size(); t++) {
                int tt = 0;
                while (storedFileLocators.size() < fileLocators.get(t).size() && storedFileLocators.size() < 11
                        && tt < 11) {
                    if (fileLocators.get(t).get(elementXmlTag.toUpperCase() + tt) != null
                            && new File(fileLocators.get(t).get(elementXmlTag.toUpperCase() + tt)).exists()) {
                        if (excludedValue == null || excludedValue.isEmpty() || !excludedValue
                                .equalsIgnoreCase(fileLocators.get(t).get(elementXmlTag.toUpperCase() + tt))) {
                            storedFileLocators.add(fileLocators.get(t).get(elementXmlTag.toUpperCase() + tt));
                        }
                    }
                    tt++;
                }
            }
        }
        return storedFileLocators;
    }

    public static Document createXmlDocument() {
        return createXmlDocument(null);
    }

    public static Document createXmlDocument(String xmlTag) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            MyDialog.showException(e, "Error thrown during the initialisation of the XML parser");
            return null;
        }

        Document doc = builder.newDocument();
        Element theRoot = doc.createElement("ROOT");
        if (xmlTag != null) {
            Element fileLocDef = doc.createElement(xmlTag);
            theRoot.appendChild(fileLocDef);
        }
        doc.appendChild(theRoot);
        return doc;
    }

    public static Document getXmlDocument(String filePath) {
        String content = Utils.getContentFromFile(filePath);
        if (content != null && !content.toLowerCase().contains("<root>"))
            return null;

        // soll auch ohne filePath auskommen und ein leeres doc erzeugen
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            MyDialog.showException(e, "Error thrown during the initialisation of the XML parser");
            return null;
        }
        Document doc = null;

        try {
            if (filePath != null && filePath.trim().startsWith("<")) { // if filePath is xml content
                InputStream targetStream = new ByteArrayInputStream(filePath.getBytes());
                doc = builder.parse(targetStream);
            } else if (filePath != null && new File(filePath).length() > 0) { // if filePath is xml content
                doc = builder.parse(new File(filePath));
            } else {
                doc = createXmlDocument();
            }
        } catch (SAXException e) {
            MyDialog.showException(e, "Error thrown during parsing of the XML data in" + filePath);
        } catch (IOException e) {
            MyDialog.showException(e, "I/O thrown during loading the data from " + filePath);
        }
        return doc;
    }

    public static Map<String, List<Map<String, String>>> loadXmlModules(String filePath) {
        Map<String, List<Map<String, String>>> result = loadXmlModules(XMLUtil.getXmlDocument(filePath),
                Settings.TagMODULE, Settings.TagTABLEEXTRACTDEFINITION);
        LogManager.getLogger()
                .debug("Load XML File: " + filePath + ". Found " + String.valueOf(result.keySet().size()) + " items.");

        return result;
    }

    public static Map<String, List<Map<String, String>>> loadXmlModules(String filePath, String moduleTag,
            String childTag) {
        return loadXmlModules(XMLUtil.getXmlDocument(filePath), moduleTag, childTag);
    }

    public static Map<String, List<Map<String, String>>> loadXmlModules(String filePath, String childTag) {
        return loadXmlModules(XMLUtil.getXmlDocument(filePath), Settings.TagMODULE, childTag);
    }

    public static Map<String, List<Map<String, String>>> loadXmlModules(Node node, String childTag) {
        return loadXmlModules(node, Settings.TagMODULE, childTag);
    }

    public static Map<String, List<Map<String, String>>> loadXmlModules(Node node, String moduleTag, String childTag) {
        Map<String, List<Map<String, String>>> modules = new TreeMap<String, List<Map<String, String>>>();
        List<Node> modelNodes = XMLUtil.getElementsByTagName(node, moduleTag);
        if (modelNodes == null)
            return modules;
        for (Node currNode : modelNodes) {
            String moduleName = "";
            for (int t = 0; t < currNode.getAttributes().getLength(); t++) {
                if (Utils.equalsWithSeperator(currNode.getAttributes().item(t).getNodeName(), Settings.TagMODULENAME))
                    moduleName = currNode.getAttributes().item(t).getNodeValue();
            }
            if (moduleName == null || moduleName.isEmpty())
                moduleName = "_";

            List<Map<String, String>> modelDef = XMLUtil.loadXmlTags(currNode, childTag);
            if (modelDef != null) {
                if (modules.get(moduleName) != null) {
                    List<Map<String, String>> tmp = modules.get(moduleName);
                    for (Map<String, String> tmpE : tmp) {
                        modelDef.add(tmpE);
                    }
                }
                modules.put(moduleName, modelDef);
            }

            currNode = currNode.getNextSibling();
        }
        return modules;
    }

    public static List<Map<String, String>> loadXmlTags(String file, String mainXmlTag) {
        return loadXmlTags(getXmlDocument(file), mainXmlTag);
    }

    public static List<Map<String, String>> loadXmlTags(Node node, String mainXmlTag) {
        List<Map<String, String>> xmlExtrakt = new LinkedList<Map<String, String>>();
        Map<String, String> tagsAndContents = new HashMap<String, String>();
        XMLUtil.extractXmlTags(node, 0, tagsAndContents, xmlExtrakt, false, mainXmlTag);

        if (tagsAndContents.size() > 0) {
            xmlExtrakt.add(new MyTreeMap(tagsAndContents));
            tagsAndContents.clear();
        }

        return xmlExtrakt;
    }

    public static void extractXmlTags(Node node, int level, Map<String, String> tagsAndContents,
            List<Map<String, String>> xmlExtrakt, boolean definitionBegins, String mainXmlTag) {
        // System.out.println(level+"\n Node Text Content: "+node.getTextContent());
        if (tagsAndContents == null)
            tagsAndContents = new HashMap<String, String>();
        boolean checkAttributes = false;
        if (node == null)
            return;

        if (node.hasAttributes() && Utils.equalsWithSeperator(node.getNodeName(), mainXmlTag)) {
            definitionBegins = true;
            checkAttributes = true;
            if (tagsAndContents.size() > 0) {
                xmlExtrakt.add(new MyTreeMap(tagsAndContents));
                tagsAndContents.clear();
            }
            for (int t = 0; t < node.getAttributes().getLength(); t++) {
                String tag = node.getAttributes().item(t).getNodeName().toUpperCase();
                String content = node.getAttributes().item(t).getNodeValue();
                if (content != null && !content.isEmpty()) {
                    if (tagsAndContents.containsKey(tag))
                        tagsAndContents.put(tag, tagsAndContents.get(tag) + Settings.dataSeperator + content.trim());
                    else
                        tagsAndContents.put(tag, content.trim()); // add Key uppercase but Value normal
                }
            }

        }

        if (node.hasChildNodes()) {
            NodeList children = node.getChildNodes();

            if (Utils.equalsWithSeperator(node.getNodeName(), mainXmlTag)) {
                definitionBegins = true;
                if (children.getLength() > 1) {
                    if (!checkAttributes) {
                        if (tagsAndContents.size() > 0) {
                            xmlExtrakt.add(new MyTreeMap(tagsAndContents));
                            tagsAndContents.clear();
                        }
                        if (node.hasAttributes()) {
                            for (int t = 0; t < node.getAttributes().getLength(); t++) {
                                String tag = node.getAttributes().item(t).getNodeName().toUpperCase();
                                String content = node.getAttributes().item(t).getNodeValue();
                                if (content != null && !content.isEmpty()) {
                                    if (tagsAndContents.containsKey(tag))
                                        tagsAndContents.put(tag,
                                                tagsAndContents.get(tag) + Settings.dataSeperator + content.trim());
                                    else
                                        tagsAndContents.put(tag, content.trim()); // add Key uppercase but Value normal
                                }
                            }
                        }
                    }
                } else if (node.getTextContent() != null && !node.getTextContent().trim().isEmpty()) {
                    tagsAndContents.put("_", node.getTextContent().trim());
                }
            } else if (children.getLength() == 1 && definitionBegins) {
                String tag = node.getNodeName().toUpperCase();
                String content = node.getTextContent();
                if (content != null && !content.isEmpty()) {
                    if (tagsAndContents.containsKey(tag))
                        tagsAndContents.put(tag, tagsAndContents.get(tag) + Settings.dataSeperator + content.trim());
                    else
                        tagsAndContents.put(tag, content.trim()); // add Key uppercase but Value normal
                }
            }

            for (int t = 0; t < children.getLength(); t++) {
                extractXmlTags(children.item(t), level + 1, tagsAndContents, xmlExtrakt, definitionBegins, mainXmlTag);
            }
        }
    }

    public static List<Map<String, String>> loadDefinitions(Document doc, String definition,
            List<Map<String, String>> xmlExtraktDefault) {
        List<Map<String, String>> xmlExtrakt = xmlExtraktDefault != null ? xmlExtraktDefault
                : new LinkedList<Map<String, String>>();
        if (doc == null)
            return xmlExtrakt;

        try {
            // List<Map<String, String>> includes = loadDefinitions(doc,
            // Settings.TagINCLUDE, null);

            Map<String, String> tagsAndContents = new MyTreeMap();
            extractXmlTags(doc, 0, tagsAndContents, xmlExtrakt, false, definition);
            if (tagsAndContents.size() > 0) {
                xmlExtrakt.add(new MyTreeMap(tagsAndContents));
                tagsAndContents.clear();
            } // System.out.println(xmlExtrakt);
            return xmlExtrakt;
        } catch (Exception ex) {
            // ErrorMessage.showException(ex, "Load " + definition + " failed.");
        }
        return xmlExtrakt;
    }

    public static List<Map<String, String>> loadDefinitions(String filePath, String definition) {
        if (definition == null || definition.isEmpty())
            return new LinkedList<Map<String, String>>();

        if (definition.equalsIgnoreCase(Settings.TagSQLQUERYDEFINITION)) {
            Map<String, List<Map<String, String>>> modules = XMLUtil.loadXmlModules(filePath);
            if (modules != null && modules.size() > 0) {
                List<Map<String, String>> list = new LinkedList<Map<String, String>>();
                for (Map.Entry<String, List<Map<String, String>>> entry : modules.entrySet()) {
                    for (Map<String, String> planDef : entry.getValue()) {
                        list.add(planDef);
                    }
                }
                return list;
            }
        }

        Document doc = getXmlDocument(filePath);
        return loadDefinitions(doc, definition);
    }

    public static Boolean hasDefinitions(String filePath, String definition) {
        return !loadDefinitions(filePath, definition).isEmpty();
    }

    public static List<Map<String, String>> loadDefinitions(Document doc, String definition) {
        return loadDefinitions(doc, definition, null);
    }

    private static Node tmpNode = null;

    public static Node getXMLNodeByTagName(Node node, String tagName) {
        if (node == null)
            return null;
        if (Utils.equalsWithSeperator(node.getNodeName(), tagName))
            return node;
        else {
            tmpNode = null;
            rekgetXMLNodeByTagName(node, tagName);
            return tmpNode;
        }
    }

    public static void rekgetXMLNodeByTagName(Node node, String tagName) {
        if (node == null)
            return;

        for (int t = 0; t < node.getChildNodes().getLength(); t++) {
            if (Utils.equalsWithSeperator(node.getChildNodes().item(t).getNodeName(), tagName))
                tmpNode = node.getChildNodes().item(t);
            else
                rekgetXMLNodeByTagName(node.getChildNodes().item(t), tagName);
        }
    }

    public static Node getXMLNodeByTagName(Node node, String tagName, String tagValue) {
        if (node == null)
            return null;

        if (node.getNodeType() == 1 && Utils.equalsWithSeperator(node.getNodeName(), tagName)
                && Utils.equalsWithSeperator(node.getFirstChild().getNodeValue(), tagValue))
            return node;
        else {
            tmpNode = null;
            rekgetXMLNodeByTagName(node, tagName, tagValue);
            return tmpNode;
        }
    }

    public static void rekgetXMLNodeByTagName(Node node, String tagName, String tagValue) {
        if (node == null)
            return;
        if (node.getNodeType() == 1 &&
                node.getNodeName().toUpperCase().equals(tagName.toUpperCase()) &&
                node.hasChildNodes()) {
            for (int t = 0; t < node.getChildNodes().getLength(); t++) {
                if (node.getChildNodes().item(t).getNodeValue().trim().equals(tagValue.trim()))
                    tmpNode = node.getChildNodes().item(t);
                else
                    rekgetXMLNodeByTagName(node.getChildNodes().item(t), tagName, tagValue);
            }
        } else
            for (int t = 0; t < node.getChildNodes().getLength(); t++) {
                rekgetXMLNodeByTagName(node.getChildNodes().item(t), tagName, tagValue);
            }
    }

    public static List<Node> getElementsByTagName(Node node, String tagName) {
        if (node == null)
            return null;
        List<Node> result = new LinkedList<Node>();
        if (Utils.equalsWithSeperator(node.getNodeName(), tagName))
            result.add(node);
        rekGetElementsByTagName(node, tagName, result);
        return result;
    }

    public static void rekGetElementsByTagName(Node node, String tagName, List<Node> result) {
        if (node == null)
            return;
        for (int t = 0; t < node.getChildNodes().getLength(); t++) {
            if (Utils.containsIgnoreCase(node.getChildNodes().item(t).getNodeName(), tagName))
                result.add(node.getChildNodes().item(t));
            rekGetElementsByTagName(node.getChildNodes().item(t), tagName, result);
        }
    }

    public static String prettyXMLFormat(String input) {
        return input;
        // return prettyXMLFormat(input, 2);
    }

    public static String prettyXMLFormat(String input, int indent) {
        try {

            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, Settings.YES);
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            MyDialog.showException(e, "Error when formatting XML: " + input);
            return null; // simple exception handling, please review it
        }
    }

    // public static String formatXML(String unformattedXml) {
    // try {
    // final Document document = parseXmlFile(unformattedXml);

    // OutputFormat format = new OutputFormat(document);
    // format.setLineWidth(65);
    // format.setIndenting(true);
    // format.setIndent(2);
    // Writer out = new StringWriter();
    // XMLSerializer serializer = new XMLSerializer(out, format);
    // serializer.serialize(document);

    // return out.toString();
    // } catch (IOException e) {
    // throw new RuntimeException(e);
    // }
    // }

    // public static Document parseXmlFile(String in) {
    // try {
    // DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    // DocumentBuilder db = dbf.newDocumentBuilder();
    // InputSource is = new InputSource(new StringReader(in));
    // return db.parse(is);
    // } catch (ParserConfigurationException e) {
    // throw new RuntimeException(e);
    // } catch (SAXException e) {
    // throw new RuntimeException(e);
    // } catch (IOException e) {
    // throw new RuntimeException(e);
    // }
    // }

}