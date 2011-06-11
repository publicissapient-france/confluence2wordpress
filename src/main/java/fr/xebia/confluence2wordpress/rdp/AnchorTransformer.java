/**
 * 
 */
package fr.xebia.confluence2wordpress.rdp;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

/**
 * @author Alexandre Dutra
 */
public class AnchorTransformer implements TagNodeVisitor {

    /**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            if ("a".equals(tag.getName())) {
                String name = tag.getAttributeByName("name");
                if (name != null && name.startsWith("DRAFT-Revuedepresse-")) {
                    String newName = StringUtils.substringAfter(name, "DRAFT-Revuedepresse-");
                    tag.setAttribute("name", newName);
                }
            }
        }
        return true;
    }

}
