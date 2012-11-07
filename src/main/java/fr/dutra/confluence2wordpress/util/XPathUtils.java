package fr.dutra.confluence2wordpress.util;

import java.io.Reader;
import java.io.StringReader;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class XPathUtils {

    private static final XPathFactory FACTORY;
    
    static {
    	FACTORY = XPathFactory.newInstance();
    }
    
    public static NodeList evaluateXPathAsNodeList(Reader reader, String xpath) throws XPathExpressionException {
    	return (NodeList) FACTORY.newXPath().compile(xpath).evaluate(new InputSource(reader), XPathConstants.NODESET);
    }
    
    public static NodeList evaluateXPathAsNodeList(Node node, String xpath) throws XPathExpressionException {
    	return (NodeList) FACTORY.newXPath().compile(xpath).evaluate(node, XPathConstants.NODESET);
    }
    
    public static String evaluateXPathAsString(String xml, String xpath) throws XPathExpressionException {
    	return FACTORY.newXPath().compile(xpath).evaluate(new InputSource(new StringReader(xml)));
    }

    public static int evaluateXPathAsInt(String xml, String xpath) throws XPathExpressionException {
    	Number d = (Number) FACTORY.newXPath().compile(xpath).evaluate(new InputSource(new StringReader(xml)), XPathConstants.NUMBER);
		return d.intValue();
    }

}
