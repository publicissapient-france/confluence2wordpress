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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.xebia.confluence2wordpress.util.collections.MapUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class SyntaxHighlighterConverter implements TagNodeVisitor {

    /**
        <div class="code panel" style="border-width: 1px;">
            <div class="codeContent panelContent">
                <script type="syntaxhighlighter" class="theme: Confluence; brush: xml; gutter: false"><![CDATA[&lt;plugin&gt;
                code
                ]]></script>
            </div>
        </div>

        Converts to

        [xml]
        code
        [/xml]

     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode divCodePanel = (TagNode) htmlNode;
            String tagName = divCodePanel.getName();
            if ("div".equals(tagName) && divCodePanel.getAttributeByName("class").contains("code panel")) {
                TagNode divCodeContent = divCodePanel.findElementByName("div", false);
                if (divCodeContent.getAttributeByName("class").contains("codeContent")) {
                    String code = findCode(divCodeContent);
                    if(code != null) {
                        String brush = findBrush(divCodeContent);
                        StringBuilder sb = new StringBuilder();
                        String replacement = sb.append('[').append(brush).append("]\n").append(code).append("\n[/").append(brush).append(']').toString();
                        parentNode.replaceChild(divCodePanel, new ContentNode(replacement));
                    }
                }
            }
        }
        return true;
    }

    private String findBrush(TagNode divCodeContent) {
        String brush = "text";
        TagNode divError = divCodeContent.findElementByAttValue("class", "error", false, true);
        if(divError != null) {
            brush = StringUtils.substringBetween(divError.getText().toString(), ": ", ".");
        } else {
            TagNode script = divCodeContent.findElementByName("script", false);
            if(script != null && "syntaxhighlighter".equalsIgnoreCase(script.getAttributeByName("type"))) {
                String className = script.getAttributeByName("class");
                if(className != null) {
                    Map<String, String> map = MapUtils.split(className, ";", ":");
                    brush = map.get("brush");
                }
            }
        }
        return brush;
    }

    private String findCode(TagNode divCodeContent) {
        TagNode script = divCodeContent.findElementByName("script", false);
        if(script != null && "syntaxhighlighter".equalsIgnoreCase(script.getAttributeByName("type"))) {
            String code = script.getText().toString();
            if(code.startsWith("<![CDATA[") && code.endsWith("]]>")) {
                code = StringUtils.substringBetween(code, "<![CDATA[", "]]>");
            }
            return StringEscapeUtils.unescapeHtml(StringUtils.trim(code));
        }
        return null;
    }

}
