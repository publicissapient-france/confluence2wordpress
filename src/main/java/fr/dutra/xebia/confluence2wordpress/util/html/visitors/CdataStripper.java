/**
 * 
 */
package fr.dutra.xebia.confluence2wordpress.util.html.visitors;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.dutra.xebia.confluence2wordpress.util.string.XmlEscapeUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class CdataStripper implements TagNodeVisitor {

    /**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof ContentNode) {
            ContentNode tag = (ContentNode) htmlNode;
            String code = tag.getContent().toString();
            if(code.startsWith("<![CDATA[") && code.endsWith("]]>")) {
                code = StringUtils.substringBetween(code, "<![CDATA[", "]]>");
                code = XmlEscapeUtils.escapeText(code);
                ContentNode replacement = new ContentNode(code);
                parentNode.replaceChild(tag, replacement);
            }
        }
        return true;
    }

}
