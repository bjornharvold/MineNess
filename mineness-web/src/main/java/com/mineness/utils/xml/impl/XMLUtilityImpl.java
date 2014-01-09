package com.mineness.utils.xml.impl;

import com.mineness.domain.enums.NavigationCommand;
import com.mineness.utils.xml.XMLUtility;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Bjorn Harvold
 * Date: 5/18/13
 * Time: 7:45 PM
 * Responsibility:
 */
@Component("xmlUtility")
public class XMLUtilityImpl implements XMLUtility {
    private final static Logger log = LoggerFactory.getLogger(XMLUtilityImpl.class);

    /**
     * Takes non-strict html and tries to create xhtml out of it. Returns the DOM Document
     *
     * @param reader reader
     * @return W3C Document
     */
    @Override
    public Document normalizeHtmlContent(Reader reader) {
        StringWriter writer = new StringWriter();

        // tidy up
        Tidy tidy = new Tidy();
        tidy.setXHTML(true);
        tidy.setOnlyErrors(true);
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);

        Document doc = tidy.parseDOM(reader, writer);

        if (log.isTraceEnabled()) {
            tidy.pprint(doc, System.out);
        }

        return doc;
    }

    @Override
    public Document normalizeHtmlContent(String html) {
        StringReader reader = new StringReader(html);
        StringWriter writer = new StringWriter();

        // tidy up
        Tidy tidy = new Tidy();
        tidy.setXHTML(true);
        tidy.setOnlyErrors(true);
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);

        Document doc = tidy.parseDOM(reader, writer);

        if (log.isTraceEnabled()) {
            tidy.pprint(doc, System.out);
        }

        return doc;
    }

    @Override
    public void printXHTMLDocument(Document doc, OutputStream out) {
        Tidy tidy = new Tidy();
        tidy.setXHTML(true);
        tidy.setOnlyErrors(true);
        tidy.setQuiet(true);
        tidy.setShowWarnings(false);

        if (log.isDebugEnabled()) {
            tidy.pprint(doc, out);
        }
    }

    /**
     * Prints w3c xml document to System.out
     *
     * @param doc doc
     */
    @Override
    public void printXMLDocument(Document doc) throws TransformerException, UnsupportedEncodingException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
    }

    /**
     * Prints w3c document to specified output stream
     *
     * @param doc doc
     * @param out out
     * @throws IOException
     * @throws TransformerException
     */
    @Override
    public void printXMLDocument(Document doc, OutputStream out) throws TransformerException, UnsupportedEncodingException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }

    /**
     * Convenience method for finding a list of values in an xml document
     *
     * @param doc        doc
     * @param xpathQuery xpathQuery
     * @return NodeList
     * @throws XPathExpressionException
     */
    @Override
    public NodeList findNodeList(Document doc, String xpathQuery) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (NodeList) xPath.evaluate(xpathQuery, doc.getDocumentElement(), XPathConstants.NODESET);
    }

    /**
     * Convenience method for finding a value in an xml document
     *
     * @param doc        doc
     * @param xpathQuery xpathQuery
     * @return Node
     * @throws XPathExpressionException
     */
    @Override
    public Node findNode(Document doc, String xpathQuery) throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        return (Node) xPath.evaluate(xpathQuery, doc.getDocumentElement(), XPathConstants.NODE);
    }

    /**
     * Advanced method to navigate DOM tree and retrieve a value
     * Requirement: the xpath query should only give one Node as its result. make sure the xpath query is specific enough
     *
     * @param doc        xml doc
     * @param xpathQuery xpath query to start things off
     * @param navigation custom navigation language to continue when xpath left off
     * @return
     */
    @Override
    public String findValue(Document doc, String xpathQuery, String navigation) throws XPathExpressionException {
        String result = null;

        Node node = findNode(doc, xpathQuery);

        if (node != null && StringUtils.isNotBlank(navigation)) {
            // start walking the DOM tree according to the navigation instructions

            String[] commands = StringUtils.split(navigation, '|');

            result = recursiveNodeCommandWalker(node, commands, 0);
        }

        return result;
    }

    /**
     * Every command list has to end with a string related result
     * @param node node
     * @param commands commands
     * @param commandPosition commandPosition
     * @return
     */
    private String recursiveNodeCommandWalker(Node node, String[] commands, int commandPosition) {
        String command = commands[commandPosition];

        try {
            switch (NavigationCommand.valueOf(command)) {
                case parent:
                    node = node.getParentNode();
                    return recursiveNodeCommandWalker(node, commands, ++commandPosition);
                case first_sibling:
                    node = node.getNextSibling();
                    return recursiveNodeCommandWalker(node, commands, ++commandPosition);
                case first_child:
                    node = node.getFirstChild();
                    return recursiveNodeCommandWalker(node, commands, ++commandPosition);
                case all_siblings:
                    NodeList children = node.getChildNodes();
                    String result = null;

                    if (children.getLength() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < children.getLength(); i++) {
                            Node child = children.item(i);

                            if (StringUtils.isNotBlank(child.getNodeValue())) {
                                sb.append(StringUtils.trimToEmpty(child.getNodeValue()));
                                sb.append("\n");
                            }
                        }
                        result = sb.toString();
                    }

                    return result;
                case nodevalue:
                    return recursiveStringCommandDecider(node.getNodeValue(), commands, commandPosition);


            }
        } catch (IllegalArgumentException ex) {
            // this does not mean there is an error, only that the command is not a simple dom walk

            // let's check to see if there is an @ sign at the start of the command - that means we want an attribute
            if (StringUtils.startsWith(command, "@")) {
                node = node.getAttributes().getNamedItem(StringUtils.substring(command, 1));

                return recursiveNodeCommandWalker(node, commands, ++commandPosition);
            }
        }

        return null;
    }

    private String recursiveStringCommandDecider(String result, String[] commands, int commandPosition) {
        // the following commands could be string related
        if (commandPosition+1 < commands.length) {
            // there are more string commands to apply here
            return recursiveStringCommandWalker(result, commands, ++commandPosition);
        } else {
            // we can safely return the string
            return result;
        }
    }

    private String recursiveStringCommandWalker(String string, String[] commands, int commandPosition) {
        String command = commands[commandPosition];

        if (StringUtils.startsWith(command, "substring")) {
            string = substringCommand(command, string);
        }

        // the following commands could be string related
        return recursiveStringCommandDecider(string, commands, commandPosition);
    }

    /**
     * Handles command of type substring
     * @param command command
     * @param string string
     * @return
     */
    private String substringCommand(String command, String string) {
        int substring1Start = StringUtils.indexOf(command, "'");
        int substring1End = StringUtils.indexOf(command, "'", substring1Start+1);
        int substring2Start = StringUtils.indexOf(command, "'", substring1End+1);
        int substring2End = StringUtils.indexOf(command, "'", substring2Start+1);

        String substring1 = StringUtils.substring(command, substring1Start+1, substring1End);
        String substring2 = StringUtils.substring(command, substring2Start+1, substring2End);

        int start = StringUtils.indexOf(string, substring1);
        int end = StringUtils.indexOf(string, substring2, substring1.length());

        return StringUtils.substring(string, start + substring1.length(), end);

    }

    public static void main(String[] args) {
        XMLUtilityImpl impl = new XMLUtilityImpl();

        System.out.println(impl.substringCommand("substring('http://www.amazon.com/dp/', '/')", "http://www.amazon.com/dp/B000U69B0Y/ref=pe_175190_21431760_M3T1_SC_dp_1"));
    }

}
