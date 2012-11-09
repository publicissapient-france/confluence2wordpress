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
/**
 * 
 */
package fr.dutra.confluence2wordpress.core.converter.visitors;

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.dutra.confluence2wordpress.util.UrlUtils;


/**
 * Translates links to the Confluence page being synchronized into a Wordpress "[permalink]" macro,
 * so that it can be correctly rendered on the Wordpress side.
 * 
 * @author Alexandre Dutra
 *
 */
public class PermalinkProcessor implements TagNodeVisitor {

    private static final String A = "a";

	private static final String HREF = "href";

	private static final String PERMALINK = "[permalink]";
	
	private final String pageUrl;
    
	private final String confluenceRootUrl;
	
    public PermalinkProcessor(String pageUrl, String confluenceRootUrl) {
    	this.pageUrl = pageUrl;
    	this.confluenceRootUrl = confluenceRootUrl;
    }

    /**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            String tagName = tag.getName();
            if (A.equals(tagName)) {
                String url = tag.getAttributeByName(HREF);
                if (url != null) {
                	String absolute = UrlUtils.absolutize(url, confluenceRootUrl);
                	if(absolute.startsWith(pageUrl))
                	tag.setAttribute(HREF, absolute.replace(pageUrl, PERMALINK));
                }
            }
        }
        return true;
    }


}
