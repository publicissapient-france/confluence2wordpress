package fr.dutra.confluence2wordpress.util;

import java.io.StringReader;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;


public class XPathUtils {

    private static final XPathFactory FACTORY;
    
    static {
    	FACTORY = XPathFactory.newInstance();
    }
    
    public static String evaluateXPathAsString(String xml, String xpath) throws XPathExpressionException {
    	return FACTORY.newXPath().compile(xpath).evaluate(new InputSource(new StringReader(xml)));
    }

    public static int evaluateXPathAsInt(String xml, String xpath) throws XPathExpressionException {
    	Double d = (Double) FACTORY.newXPath().compile(xpath).evaluate(new InputSource(new StringReader(xml)), XPathConstants.NUMBER);
		return d.intValue();
    }

}
