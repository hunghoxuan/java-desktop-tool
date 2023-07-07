package com.rs2.modules.files.isoparser.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rs2.modules.files.FilesService;
import com.rs2.modules.files.isoparser.Trace;
import com.rs2.modules.files.isoparser.elements.iso.IsoField;
import com.rs2.modules.files.isoparser.elements.iso.IsoFile;
import com.rs2.modules.files.isoparser.elements.iso.IsoMessage;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class XmlReader {

    public static IsoFile read(String source) throws Exception {
        Trace.log("XmlReader", "Reading XML representation");
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(new BufferedInputStream(new FileInputStream(new File(source))));
            Node root = doc.getDocumentElement();
            if (!root.getNodeName().equals("IsoFile")) {
                Trace.log("XmlReader", "Wrong root element: " + root.getNodeName());
                throw new IOException("Wrong root node name");
            }

            IsoFile isoFile = new IsoFile();

            NodeList messages = root.getChildNodes();

            Trace.log("XmlReader", "Iterating message elements");

            for (int messageIndex = 0; messageIndex < messages.getLength(); messageIndex++) {
                Node message = messages.item(messageIndex);
                if (!message.getNodeName().equals("IsoMessage")) {
                    Trace.log("XmlReader", "Wrong element name: " + message.getNodeName() + ". Skipping.");
                    continue;
                }

                IsoMessage isoMessage = IsoMessage.newInstance(FilesService.TYPE_MASTERCARD);
                {
                    Trace.log("XmlReader", "Reading message attributes");
                    NamedNodeMap attrs = message.getAttributes();
                    for (int attributeIndex = 0; attributeIndex < attrs.getLength(); attributeIndex++) {
                        Node attr = attrs.item(attributeIndex);
                        switch (attr.getNodeName()) {
                            case "Mti":
                                Trace.log("XmlReader", "MTI attribute found");
                                isoMessage.setMti(attr.getNodeValue());
                                break;
                            case "Bitmap":
                                Trace.log("XmlReader", "MTI Bitmap found");
                                isoMessage.setBitmapPrimary(attr.getNodeValue());
                                break;
                            case "Bitmap2":
                                Trace.log("XmlReader", "MTI second Bitmap found");
                                isoMessage.setBitmapSecondary(attr.getNodeValue());
                                break;
                            default:
                                Trace.log("XmlReader", "Unknown attribute: " + attr.getNodeName());
                        }
                    }
                }

                Trace.log("XmlReader", "Iterating fields elements");
                NodeList fields = message.getChildNodes();

                for (int fieldIndex = 0; fieldIndex < fields.getLength(); fieldIndex++) {
                    Node field = fields.item(fieldIndex);
                    if (!field.getNodeName().equals("IsoField")) {
                        Trace.log("XmlReader", "Wrong element name: " + field.getNodeName());
                        continue;
                    }

                    String name = null;
                    String index = null;

                    String hex = null;
                    String val = null;

                    Trace.log("XmlReader", "Reading field attributes");
                    NamedNodeMap attrs = field.getAttributes();
                    for (int attributeIndex = 0; attributeIndex < attrs.getLength(); attributeIndex++) {
                        Node attr = attrs.item(attributeIndex);
                        switch (attr.getNodeName()) {
                            case "Key":
                            case "Index":
                                Trace.log("XmlReader", "Found field index");
                                index = attr.getNodeValue();
                                break;
                            case "Name":
                                Trace.log("XmlReader", "Found field name");
                                name = attr.getNodeValue();
                                break;
                            case "Value":
                                Trace.log("XmlReader", "Found field value");
                                val = attr.getNodeValue();
                                break;
                            default:
                                Trace.log("XmlReader", "Unknown field attribute: " + attr.getNodeName());
                        }
                    }

                    Trace.log("XmlReader", "Looking for child nodes of field (raw binary data)");
                    for (int k = 0; k < field.getChildNodes().getLength(); k++) {
                        Node sub = field.getChildNodes().item(k);
                        switch (sub.getNodeName()) {
                            case "Binary":
                                Trace.log("XmlReader", "Found binary data");
                                hex = sub.getChildNodes().item(0).getNodeValue();
                                break;
                        }
                    }

                    if (name == null) {
                        Trace.log("XmlReader", "Field name undefined");
                        throw new IOException("No field name defined");
                    }

                    if (val == null && hex == null) {
                        Trace.log("XmlReader", "Field value undefined");
                        throw new IOException("No field value defined");
                    }

                    IsoField isoField = new IsoField();
                    if (index != null)
                        isoField.setIndex(Integer.parseInt(index));

                    isoField.setName(name);

                    if (hex != null) {
                        Trace.log("XmlReader", "Using binary data");
                        isoField.setRawData(FilesService.hexToBytes(hex));
                    } else {
                        Trace.log("XmlReader", "Using readable value");
                        isoField.setData(val);
                    }

                    isoMessage.addField(isoField);
                }

                isoMessage.number = messageIndex + 1;

                isoFile.getMessages().add(isoMessage);
            }

            Trace.log("XmlReader", "XML representation load ok");
            return isoFile;

        } catch (Exception e) {
            Trace.log("XmlReader", "XML representation read failed");
            throw e;
        }
    }
}
