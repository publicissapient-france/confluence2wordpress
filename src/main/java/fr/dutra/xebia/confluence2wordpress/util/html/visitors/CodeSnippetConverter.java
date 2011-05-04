/**
 * 
 */
package fr.dutra.xebia.confluence2wordpress.util.html.visitors;

import java.util.Map;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.dutra.xebia.confluence2wordpress.util.collections.MapSplitter;


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
                        Map<String, String> map = MapSplitter.split(className, ";", ":");
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
