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

import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;


/**
 * @author Alexandre Dutra
 *
 */
public class HeadingsCollector implements TagNodeVisitor {

    private static final String NAME = "name";

	private static final String A = "a";

	private static final String H3 = "h3";

	private static final String H4 = "h4";

	private Heading h3;

    private List<Heading> headings = new ArrayList<Heading>();


    /**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            if(H3.equals(tag.getName())) {
                h3 = new Heading();
                h3.setLabel(tag.getText().toString());
                headings.add(h3);
            } else if(h3 != null && H4.equals(tag.getName())) {
                Heading h4 = new Heading();
                h4.setLabel(tag.getText().toString());
                TagNode link = tag.findElementByName(A, false);
                if(link != null) {
                    String name = link.getAttributeByName(NAME);
                    h4.setAnchor(name);
                }
                h3.addChild(h4);
            }
        }
        return true;
    }

    public List<Heading> getHeadings() {
        return headings;
    }

}
