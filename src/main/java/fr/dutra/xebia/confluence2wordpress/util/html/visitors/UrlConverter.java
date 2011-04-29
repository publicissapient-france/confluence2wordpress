/**
 * 
 */
package fr.dutra.xebia.confluence2wordpress.util.html.visitors;

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.htmlcleaner.Utils;


/**
 * @author Alexandre Dutra
 *
 */
public class UrlConverter implements TagNodeVisitor {

    private String uploadedFilesBaseUrl;


    public UrlConverter(String uploadedFilesBaseUrl) {
        super();
        this.uploadedFilesBaseUrl = uploadedFilesBaseUrl;
    }


    public String getUploadedFilesBaseUrl() {
        return uploadedFilesBaseUrl;
    }


    public void setUploadedFilesBaseUrl(String baseUrl) {
        this.uploadedFilesBaseUrl = baseUrl;
    }

    /**
     * @inheritdoc
     */
    @Override
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            String tagName = tag.getName();
            if ("img".equals(tagName)) {
                String src = tag.getAttributeByName("src");
                if (src != null) {
                    tag.setAttribute("src", Utils.fullUrl(uploadedFilesBaseUrl, src));
                }
            }
        }
        return true;
    }

}
