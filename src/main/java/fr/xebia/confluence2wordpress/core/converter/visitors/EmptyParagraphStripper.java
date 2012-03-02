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

import java.util.List;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;


/**
 * @author Alexandre Dutra
 *
 */
public class EmptyParagraphStripper extends EmptyTagStripperBase {

    private static final String TWO_LINE_BREAKS = "\n\n";

	/**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            if("p".equals(tag.getName()) && hasNoAttributes(tag)) {
                stripTag(parentNode, tag);
            }
        }
        return true;
    }

	private void stripTag(TagNode parentNode, TagNode tag) {
		@SuppressWarnings("unchecked")
		List<HtmlNode> children = tag.getChildren();
		for (HtmlNode child : children) {
		    parentNode.insertChildAfter(tag, child);
		    parentNode.insertChildAfter(child, new ContentNode(TWO_LINE_BREAKS));
		}
		parentNode.removeChild(tag);
	}

}
