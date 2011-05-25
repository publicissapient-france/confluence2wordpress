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
package fr.dutra.xebia.wiki2html.core.visitors;

import java.util.Map;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.dutra.xebia.wiki2html.util.collections.MapUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class CodeSnippetConverter implements TagNodeVisitor {

    /*
        <div class="code panel" style="border-width: 1px;"><div class="codeContent panelContent">
        <script type="syntaxhighlighter" class="theme: Confluence; brush: java; gutter: false"><![CDATA[JAva
        Java]]></script>
        </div></div>

        [java]
        JAva
        Java
        [/java]

     */

    /**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            String tagName = tag.getName();
            if ("script".equals(tagName)) {
                String type = tag.getAttributeByName("type");
                if (type != null && "syntaxhighlighter".equalsIgnoreCase(type)) {
                    String className = tag.getAttributeByName("class");
                    if(className != null) {
                        Map<String, String> map = MapUtils.split(className, ";", ":");
                        String brush = map.get("brush");
                        if(brush == null) {
                            brush = "java";
                        }
                        String code = tag.getText().toString();
                        TagNode codePanelDiv = tag.getParent().getParent();
                        TagNode parent = codePanelDiv.getParent();
                        StringBuilder sb = new StringBuilder();
                        String replacement = sb.append('[').append(brush).append("]\n").append(code).append("\n[/").append(brush).append(']').toString();
                        parent.replaceChild(codePanelDiv, new ContentNode(replacement));
                    }
                }
            }
        }
        return true;
    }

}
