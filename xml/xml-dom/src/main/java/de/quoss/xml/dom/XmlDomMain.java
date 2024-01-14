package de.quoss.xml.dom;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Clemens Quoß
 */
public class XmlDomMain {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(XmlDomMain.class);
    
    public static void main(final String...args) throws Exception {
        LOGGER.info("Hello, XmlDomMain!");
        new XmlDomMain().run();
    }
    
    private void run() throws Exception {
        final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newDefaultInstance();
        final DocumentBuilder builder = builderFactory.newDocumentBuilder();
        final Document document = builder.newDocument();
        final Element customer = document.createElement("customer");
        final Element id = document.createElement("id");
        final Node idContent = document.createTextNode("0");
        id.appendChild(idContent);
        final Element name = document.createElement("name");
        final Node nameContent = document.createTextNode("Clemens Quoß");
        name.appendChild(nameContent);
        customer.appendChild(id);
        customer.appendChild(name);
        document.appendChild(customer);
        final TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        final DOMSource source = new DOMSource(document);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final StreamResult result = new StreamResult(stream);
        transformer.transform(source, result);
        final String output = stream.toString(StandardCharsets.UTF_8);
        LOGGER.info("xml-dom-main/run [output={}]", output);
    }
    
}
