package com.mineness.utils.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Bjorn Harvold
 * Date: 5/18/13
 * Time: 7:49 PM
 * Responsibility:
 */
public interface XMLUtility {
    Document normalizeHtmlContent(String html);

    void printXMLDocument(Document doc) throws TransformerException, UnsupportedEncodingException;

    void printXMLDocument(Document doc, OutputStream out) throws TransformerException, UnsupportedEncodingException;

    NodeList findNodeList(Document doc, String xpathQuery) throws XPathExpressionException;

    Node findNode(Document doc, String xpathQuery) throws XPathExpressionException;

    String findValue(Document doc, String xpathQuery, String navigation) throws XPathExpressionException;

    Document normalizeHtmlContent(Reader reader);

    void printXHTMLDocument(Document doc, OutputStream out);
}
