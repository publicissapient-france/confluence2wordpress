/**
 * 
 */
package fr.dutra.xebia.confluence2wordpress.util.html.visitors;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.dutra.xebia.confluence2wordpress.util.rdp.Heading;


/**
 * @author Alexandre Dutra
 *
 */
public class HeadingsCollector implements TagNodeVisitor {

    private Heading h3;

    private List<Heading> headings = new ArrayList<Heading>();


    /**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            if("h3".equals(tag.getName())) {
                h3 = new Heading();
                h3.setLabel(tag.getText().toString());
                headings.add(h3);
            } else if(h3 != null && "h4".equals(tag.getName())) {
                Heading h4 = new Heading();
                h4.setLabel(tag.getText().toString());
                TagNode link = tag.findElementByName("a", false);
                if(link != null) {
                    String name = link.getAttributeByName("name");
                    h4.setAnchor("#" + StringUtils.substringAfter(name, "DRAFT-Revuedepresse-"));
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
