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
package fr.dutra.confluence2wordpress.core.converter.visitors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.dutra.confluence2wordpress.core.sync.SynchronizedAttachment;
import fr.dutra.confluence2wordpress.util.UrlUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class AnchorProcessor implements TagNodeVisitor {

    private static final String A = "a";

	private static final String HREF = "href";

	private final Map<String, SynchronizedAttachment> synchronizedAttachments;

	private final String confluenceRootUrl;
    
    public AnchorProcessor(List<SynchronizedAttachment> synchronizedAttachments, String confluenceRootUrl) {
        super();
        this.confluenceRootUrl = confluenceRootUrl;
        this.synchronizedAttachments = new HashMap<String, SynchronizedAttachment>();
        for (SynchronizedAttachment synchronizedAttachment : synchronizedAttachments) {
			this.synchronizedAttachments.put(synchronizedAttachment.getAttachmentPath(), synchronizedAttachment);
			this.synchronizedAttachments.put(synchronizedAttachment.getThumbnailPath(), synchronizedAttachment);
		}
    }

    /**
     * @inheritdoc
     */
    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode tag = (TagNode) htmlNode;
            String tagName = tag.getName();
            if (A.equals(tagName)) {
                String url = tag.getAttributeByName(HREF);
                if (url != null) {
                    String href = findWordpressUrl(url);
                    if(href != null) {
                        tag.setAttribute(HREF, href);
                    }
                }
            }
        }
        return true;
    }

    private String findWordpressUrl(String confluenceUrl) {
    	String path = UrlUtils.extractConfluenceRelativePath(confluenceUrl, confluenceRootUrl);
    	SynchronizedAttachment synchronizedAttachment = this.synchronizedAttachments.get(path);
		if(synchronizedAttachment != null) {
            return synchronizedAttachment.getWordpressFile().getUrl();
		}
		return null;
    }

}
