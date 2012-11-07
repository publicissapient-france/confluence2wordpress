/**
 * Copyright 2011 Alexandre Dutra
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
/**
 * 
 */
package fr.dutra.confluence2wordpress.core.converter.processors;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fr.dutra.confluence2wordpress.core.converter.ConversionException;
import fr.dutra.confluence2wordpress.core.converter.ConverterOptions;
import fr.dutra.confluence2wordpress.core.converter.visitors.Heading;
import fr.dutra.confluence2wordpress.macro.Author;
import fr.dutra.confluence2wordpress.util.StaxUtils;
import fr.dutra.confluence2wordpress.util.XPathUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class HeadingsCollector implements PreProcessor {

	private static final String H3_XPATH = "//h3";

	private static final String H4_XPATH = "//h4";

	private static final String AUTHOR_XPATH = "//ac:macro[@ac:name='author']";

	private static final String PARAM_XPATH = "//ac:parameter";

    private static final String NAME = "name";

    private List<Heading> headings = new ArrayList<Heading>();

    public List<Heading> getHeadings() {
        return headings;
    }
    
	@Override
	public String preProcess(String storage, ConverterOptions options) throws ConversionException {
		try {
			NodeList h3s = XPathUtils.evaluateXPathAsNodeList(StaxUtils.getReader(storage), H3_XPATH);
			for(int i = 0; i < h3s.getLength(); i++) {
				Node h3 = h3s.item(i);
				Heading heading = getHeading(h3);
				headings.add(heading);
			}
		} catch (XPathExpressionException e) {
			throw new ConversionException(e);
		} catch (XMLStreamException e) {
			throw new ConversionException(e);
		} catch (MalformedURLException e) {
			throw new ConversionException(e);
		} catch (URISyntaxException e) {
			throw new ConversionException(e);
		}
		return null;
	}

	protected Heading getHeading(Node h3Node) throws XPathExpressionException, MalformedURLException, URISyntaxException {
		Heading heading = new Heading();
		heading.setLabel(h3Node.getTextContent());
		NodeList h4s = XPathUtils.evaluateXPathAsNodeList(h3Node, H4_XPATH);
		for(int i = 0; i < h4s.getLength(); i++) {
			Node h4 = h4s.item(i);
			Heading subHeading = getSubHeading(h4);
			heading.addChild(subHeading);
		}
		return heading;
	}

	private Heading getSubHeading(Node h4Node) throws XPathExpressionException, MalformedURLException, URISyntaxException {
		Heading heading = new Heading();
		heading.setLabel(h4Node.getTextContent());
		NodeList h4s = XPathUtils.evaluateXPathAsNodeList(h4Node, AUTHOR_XPATH);
		if(h4s.getLength() > 0) {
			Node authorNode = h4s.item(0);
			Map<String, String> parameters = getAuthorParameters(authorNode);
			Author author = Author.fromMacroParameters(parameters);
			heading.setAuthor(author);
		}
		return heading;
	}

	/*
	 * <ac:macro ac:name="author"><ac:parameter ac:name="wordpressUsername">adutra</ac:parameter></ac:macro>
	 */
	private Map<String,String> getAuthorParameters(Node authorNode) throws XPathExpressionException {
		Map<String,String> parameters = new HashMap<String, String>();
		NodeList parameterNodes = XPathUtils.evaluateXPathAsNodeList(authorNode, PARAM_XPATH);
		for(int i = 0; i < parameterNodes.getLength(); i++) {
			Node parameterNode = parameterNodes.item(i);
			String key = parameterNode.getAttributes().getNamedItem(NAME).getTextContent();
			String value = parameterNode.getTextContent();
			parameters.put(key, value);
		}
		return parameters;
	}
	
}
