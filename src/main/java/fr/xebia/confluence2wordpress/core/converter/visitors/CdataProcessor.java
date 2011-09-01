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

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.xebia.confluence2wordpress.util.html.HtmlUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class CdataProcessor implements TagNodeVisitor {

    /**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof ContentNode) {
            ContentNode tag = (ContentNode) htmlNode;
            String code = tag.getContent().toString();
            if(code.startsWith("<![CDATA[") && code.endsWith("]]>")) {
                code = StringUtils.substringBetween(code, "<![CDATA[", "]]>");
                code = HtmlUtils.escapeHtml(code);
                ContentNode replacement = new ContentNode(code);
                parentNode.replaceChild(tag, replacement);
            }
        }
        return true;
    }

}
