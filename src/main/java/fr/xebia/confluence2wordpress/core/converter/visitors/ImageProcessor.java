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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.xebia.confluence2wordpress.core.sync.SynchronizedAttachment;
import fr.xebia.confluence2wordpress.util.UrlUtils;
import fr.xebia.confluence2wordpress.wp.WordpressFile;


/**
 * @author Alexandre Dutra
 *
 */
public class ImageProcessor implements TagNodeVisitor {

    private final Map<String, SynchronizedAttachment> synchronizedAttachments;

	private final String confluenceRootUrl;
    
    public ImageProcessor(List<SynchronizedAttachment> synchronizedAttachments, String confluenceRootUrl) {
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
            if ("img".equals(tagName)) {
                String url = tag.getAttributeByName("src");
                if (url != null) {
					WordpressFile wpFile = findWordpressFile(url);
                    if(wpFile != null && wpFile.getUrl() != null) {
                    	WordpressFile src;
                    	String attr = tag.getAttributeByName("width");
                    	if(StringUtils.isNotEmpty(attr)){
                    		Integer width = StringUtils.isNotEmpty(attr) ? Integer.valueOf(attr) : null;
                    		src = wpFile.getBestAlternative(width);
                    	} else {
                    		src = wpFile;
                    	}
                        tag.setAttribute("src", src.getUrl());
                        String clazz = tag.getAttributeByName("class");
                        StringBuilder style = new StringBuilder();
                        if(clazz.contains("image-center")){
                        	style.append("margin-left:auto;margin-right:auto;display:block;");
                        }
                        if (clazz.contains("image-border")) {
                        	style.append("border:1px solid black;");
                        }
                        if(clazz.contains("image-left")){
                        	style.append("float:left; margin:0 10px 10px 0;");
                        }
                        if(clazz.contains("image-right")){
                        	style.append("float:right; margin:0 0 10px 10px;");
                        }
                    	tag.setAttribute("style", style.toString());
                        if(src.isAlternative() && ! (parentNode instanceof TagNode && ((TagNode)parentNode).getName().equals("a"))) {
                        	TagNode link = new TagNode("a");
                        	link.setAttribute("href", wpFile.getUrl());
                        	link.setAttribute("target", "_blank");
                        	link.addChild(tag);
                        	parentNode.replaceChild(tag, link);
                        }
                    }
                }
            }
        }
        return true;
    }

    private WordpressFile findWordpressFile(String confluenceUrl) {
    	String path = UrlUtils.extractConfluenceRelativePath(confluenceUrl, confluenceRootUrl);
    	SynchronizedAttachment synchronizedAttachment = this.synchronizedAttachments.get(path);
		if(synchronizedAttachment != null) {
            return synchronizedAttachment.getWordpressFile();
		}
		return null;
    }

}
