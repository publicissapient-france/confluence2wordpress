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
package fr.dutra.confluence2wordpress.core.converter.visitors;

import org.htmlcleaner.CommentNode;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.dutra.confluence2wordpress.core.converter.processors.MoreMacroPreprocessor;


/**
 * @author Alexandre Dutra
 *
 */
public class MoreMacroProcessor implements TagNodeVisitor {

    private static final String BODY = "body";
    
	private static final String MORE = "more";

	/**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            if(MoreMacroPreprocessor.WORDPRESS_MORE.equals(tag.getName())){
        		CommentNode more = new CommentNode(MORE);
        		//most often the more macro comes nested in a surrounding block tag, usually "p": we can safely delete it
            	if( ! parentNode.getName().equals(BODY) && parentNode.getChildTagList().size() == 1) {
    				parentNode.getParent().replaceChild(parentNode, more);
            	} else {
    				parentNode.replaceChild(tag, more);
            	}
            }
        }
        return true;
    }

}
