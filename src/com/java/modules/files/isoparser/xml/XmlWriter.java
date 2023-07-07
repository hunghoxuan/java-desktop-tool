package com.java.modules.files.isoparser.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.java.modules.files.FilesService;
import com.java.modules.files.isoparser.Trace;
import com.java.modules.files.isoparser.elements.iso.IsoField;
import com.java.modules.files.isoparser.elements.iso.IsoFile;
import com.java.modules.files.isoparser.elements.iso.IsoMessage;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;

public abstract class XmlWriter {

    private static DocumentBuilder db;
    private static Document doc;

    public static void write(IsoFile file, String destination) throws Exception {
        Trace.log("XmlWriter", "Writing XML representation");
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = db.newDocument();
            Element xfile = doc.createElement("IsoFile");

            writeMessages(file, xfile);

            doc.appendChild(xfile);
            Trace.log("XmlWriter", "Document preparation finished");

            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            tr.transform(new DOMSource(doc),
                    new StreamResult(new FileOutputStream(new File(destination))));

            Trace.log("XmlWriter", "File write finished");
        } catch (Exception e) {
            Trace.log("XmlWriter", "Error writing XML representation");
            throw e;
        }
    }

    public static void writeMessages(IsoFile file, Element root) {
        Trace.log("XmlWriter", "Writing messages");
        for (IsoMessage mess : file.getIsoMessages()) {
            Trace.log("XmlWriter", "Writing message, MTI: " + mess.getMti());
            Node xmess = doc.createElement("IsoMessage");
            ((Element) xmess).setAttribute("Mti", mess.getMti());
            ((Element) xmess).setAttribute("Bitmap", mess.getBitmapPrimaryAsHex());
            ((Element) xmess).setAttribute("Bitmap2", mess.getBitmapSecondaryAsHex());
            ((Element) xmess).setAttribute("Description", mess.getDescription());

            writeFields(mess, xmess);
            root.appendChild(xmess);
        }
    }

    public static void writeFields(IsoMessage mess, Node root) {
        Trace.log("XmlWriter", "Writing message fields");

        for (IsoField fld : mess.getIsoFields()) {
            Trace.log("XmlWriter", "Writing field " + fld.toString());

            Node xfield = doc.createElement("IsoField");

            String name = "";
            if (fld.getName() != null && !fld.getName().isEmpty()) {
                name = fld.getName();
            }

            if (fld.getDescription() != null && !fld.getDescription().isEmpty())
                name = name + " : " + fld.getDescription();

            ((Element) xfield).setAttribute("Name", name);

            if (fld.getIndex() != null)
                ((Element) xfield).setAttribute("Key", fld.getIndex().toString());

            if (fld.isBinary()) {
                Trace.log("XmlWriter", "Saving binary data");
                Node xdata = doc.createElement("Binary");
                xdata.appendChild(doc.createTextNode(FilesService.bytesToHex(fld.getRawData())));
                xfield.appendChild(xdata);
            } else {
                Trace.log("XmlWriter", "Saving readable value");
                ((Element) xfield).setAttribute("Value", fld.getData());
            }

            root.appendChild(xfield);
        }
    }
}
