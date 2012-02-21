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
package fr.xebia.confluence2wordpress.core.converter.visitors;

import java.util.Map;
import java.util.Map.Entry;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.xebia.confluence2wordpress.util.HtmlUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class TagAttributesProcessor implements TagNodeVisitor {

    private final Map<String,String> tagAttributes;
    
    public TagAttributesProcessor(Map<String,String> tagAttributes) {
       this.tagAttributes = tagAttributes;
    }

    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            String tagName = tag.getName();
            String attributesString = tagAttributes.get(tagName);
            if (attributesString != null) {
                Map<String, String> attributes = extractAttributes(attributesString);
            	for (Entry<String,String> entry : attributes.entrySet()) {
                	tag.setAttribute(entry.getKey(), HtmlUtils.escapeQuotes(entry.getValue()));
				}
            }
        }
        return true;
    }

	private Map<String, String> extractAttributes(String attributesString) {
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode root = cleaner.clean("<div " + attributesString + " >");
		TagNode foo = root.findElementByName("div", true);
		Map<String, String> attributes = foo.getAttributes();
		return attributes;
	}

}
