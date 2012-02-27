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

import java.util.List;

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.xebia.confluence2wordpress.core.sync.SynchronizedAttachment;
import fr.xebia.confluence2wordpress.util.UrlUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class AttachmentsProcessor implements TagNodeVisitor {

    private final List<SynchronizedAttachment> synchronizedAttachments;
    
    private final String confluenceRootUrl;

    public AttachmentsProcessor(String confluenceRootUrl, List<SynchronizedAttachment> synchronizedAttachments) {
        super();
        this.synchronizedAttachments = synchronizedAttachments;
        this.confluenceRootUrl = confluenceRootUrl;
    }


    /**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            String tagName = tag.getName();
            if ("img".equals(tagName)) {
                String url = tag.getAttributeByName("src");
                /*
                 * examples:
                 * /confluence/download/attachments/983042/image.png?version=1&amp;modificationDate=1327402370523 (image)
                 * /confluence/download/thumbnails/983042/image.png (thumbnail)
                 */
                if (url != null) {
                    Integer width = tag.getAttributeByName("width") == null ? null : Integer.valueOf(tag.getAttributeByName("width"));
                    String src = findWordpressUrl(url, width);
                    if(src != null) {
                        tag.setAttribute("src", src);
                    }
                }
            } else if ("a".equals(tagName)) {
                String url = tag.getAttributeByName("href");
                /*
                 * examples:
                 * http://localhost:1990/confluence/download/attachments/983042/image.png (image)
                 * /confluence/download/attachments/983042/pom.xml?version=1&amp;modificationDate=1327402370710 (attachment)
                 * /confluence/download/attachments/983042/armonia.png?version=1&amp;modificationDate=1327402370523 (image as attachment)
                 */
                if (url != null) {
                    String href = findWordpressUrl(url, null);
                    if(href != null) {
                        tag.setAttribute("href", href);
                    }
                }
            }
        }
        return true;
    }

    private String findWordpressUrl(String confluenceUrl, Integer width) {
    	String confluencePath = UrlUtils.extractConfluenceRelativePath(confluenceUrl, confluenceRootUrl);
    	for (SynchronizedAttachment sa : synchronizedAttachments) {
            String wordpressUrl = sa.getWordpressUrl(confluencePath, width);
            if(wordpressUrl != null){
                return wordpressUrl;
            }
        }
    	return null;
    }

}
