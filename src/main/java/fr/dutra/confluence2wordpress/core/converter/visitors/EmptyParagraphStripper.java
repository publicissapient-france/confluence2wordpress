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

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;


/**
 * @author Alexandre Dutra
 *
 */
public class EmptyParagraphStripper extends TagStripperBase {

	private static final String PARAGRAPH = "p";

	/**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            if(PARAGRAPH.equals(tag.getName())) {
            	//normally a paragraph should have at least one child character
            	//if it doesn't, it is probably a container for a deleted macro
            	//and can be safely removed
            	if(! tag.hasChildren()) {
            		parentNode.removeChild(tag);
                }
            }
        }
        return true;
    }

}
