/**
 * Copyright 2011-2012 Alexandre Dutra
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
