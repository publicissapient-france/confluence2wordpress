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

import static com.atlassian.confluence.content.render.xhtml.XhtmlConstants.CONFLUENCE_XHTML_NAMESPACE_URI;

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

import com.atlassian.confluence.links.linktypes.AbstractPageLink;
import com.atlassian.confluence.renderer.PageContext;

import fr.dutra.confluence2wordpress.core.converter.ConversionException;
import fr.dutra.confluence2wordpress.core.converter.ConverterOptions;
import fr.dutra.confluence2wordpress.macro.Author;
import fr.dutra.confluence2wordpress.util.StaxUtils;
import fr.dutra.confluence2wordpress.util.XPathUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class HeadingsCollector implements PreProcessor {

    private static final String AUTHOR = "author";

	private static final String H4 = "h4";

	private static final String H3 = "h3";

	private static final String NAME = "name";

	private static final String LOCAL_NAME_PARAMETER_XPATH = "*[local-name()='parameter']";

	private static final String MAIN_XPATH = "//*[name()='h3' or name()='h4' or name()='ac:macro']";

	private Heading currentHeading;
    
    private Heading currentSubHeading;
    
    private List<Heading> headings = new ArrayList<Heading>();

    public List<Heading> getHeadings() {
        return headings;
    }
    
	@Override
	public String preProcess(String storage, ConverterOptions options, PageContext pageContext) throws ConversionException {
		try {
			NodeList nodes = XPathUtils.evaluateXPathAsNodeList(StaxUtils.getReader(storage), MAIN_XPATH);
			for(int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if(node.getNodeName().equals(H3)){
					currentHeading = getHeading(node, pageContext);
					currentSubHeading = null;
					headings.add(currentHeading);
				} else if(node.getNodeName().equals(H4)){
					if(currentHeading != null) {
						currentSubHeading = getHeading(node, pageContext);
						currentHeading.addChild(currentSubHeading);
					}
				} else {
					String macroName = node.getAttributes().getNamedItemNS(CONFLUENCE_XHTML_NAMESPACE_URI, NAME).getTextContent();
					if(AUTHOR.equals(macroName) && 
							currentSubHeading != null && currentSubHeading.getAuthor() == null){
						Map<String, String> parameters = getAuthorParameters(node);
						Author author = Author.fromMacroParameters(parameters);
						currentSubHeading.setAuthor(author);
					}
				}
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
		return storage;
	}

	protected Heading getHeading(Node node, PageContext pageContext) throws XPathExpressionException, MalformedURLException, URISyntaxException {
		Heading heading = new Heading();
		String body = node.getTextContent();
		heading.setLabel(body);
		String anchor = AbstractPageLink.generateAnchor(pageContext, body);
		heading.setAnchor(anchor);
		return heading;
	}

	/*
	 * <ac:macro ac:name="author"><ac:parameter ac:name="wordpressUsername">adutra</ac:parameter></ac:macro>
	 */
	private Map<String,String> getAuthorParameters(Node authorNode) throws XPathExpressionException {
		Map<String,String> parameters = new HashMap<String, String>();
		NodeList parameterNodes = XPathUtils.evaluateXPathAsNodeList(authorNode, LOCAL_NAME_PARAMETER_XPATH);
		for(int i = 0; i < parameterNodes.getLength(); i++) {
			Node parameterNode = parameterNodes.item(i);
			String key = parameterNode.getAttributes().getNamedItemNS(CONFLUENCE_XHTML_NAMESPACE_URI, NAME).getTextContent();
			String value = parameterNode.getTextContent();
			parameters.put(key, value);
		}
		return parameters;
	}
	
}
