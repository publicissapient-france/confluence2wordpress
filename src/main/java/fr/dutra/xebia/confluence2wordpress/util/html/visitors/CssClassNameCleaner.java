/**
 * 
 */
package fr.dutra.xebia.confluence2wordpress.util.html.visitors;

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;


/**
 * @author Alexandre Dutra
 *
 */
public class CssClassNameCleaner implements TagNodeVisitor {

    /**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            tag.removeAttribute("class");
        }
        return true;
    }

}
