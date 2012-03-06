package fr.xebia.confluence2wordpress.util;

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

    public static void main(String[] args) throws Exception {
    	int s = XPathUtils.evaluateXPathAsInt(
    			"<div class=\"error\">boo</div>", 
    			"count(/*[name() != 'div' or @class != 'error'])");
    	System.out.println(s);
    	String s2 = XPathUtils.evaluateXPathAsString(
    			"<div class=\"error\">boo</div>", 
    			"/div[@class = 'error']");
    	System.out.println(s2);
	}
}
