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

import java.net.URL;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.xebia.confluence2wordpress.core.converter.UploadedFile;


/**
 * @author Alexandre Dutra
 *
 */
public class AttachmentsProcessor implements TagNodeVisitor {

    private List<UploadedFile> uploadedFiles;
    
    private String serverRoot;
    
    private String contextPath;

    public AttachmentsProcessor(URL confluenceRootUrl, List<UploadedFile> uploadedFiles) {
        super();
        this.uploadedFiles = uploadedFiles;
        StringBuffer result = new StringBuffer();
    	result.append(confluenceRootUrl.getProtocol());
        result.append("://");
        result.append(confluenceRootUrl.getAuthority());
        this.serverRoot = result.toString();
        this.contextPath = confluenceRootUrl.getPath();
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
                    String src = replaceAttachmentUrl(url);
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
                    String href = replaceAttachmentUrl(url);
                    if(href != null) {
                        tag.setAttribute("href", href);
                    }
                }
            }
            //TODO object
        }
        return true;
    }


    private String replaceAttachmentUrl(String url) {
    	String confluencePath = extractConfluenceRelativePath(url);
        for (UploadedFile uploadedFile : uploadedFiles) {
            String wordpressUrl = uploadedFile.getWordpressUrl(confluencePath);
			if(wordpressUrl != null) {
            	return wordpressUrl;
            }
        }
        return null;
    }


	private String extractConfluenceRelativePath(String url) {
		//url may contain "&amp;" - due to htmlcleaner?
        String path = StringEscapeUtils.unescapeXml(url);
        path = StringUtils.substringBefore(path, "?");
        if(path.startsWith(serverRoot)){
            path = StringUtils.substringAfter(path, serverRoot);
        }
        if( ! "".equals(contextPath) && path.startsWith(contextPath)){
            path = StringUtils.substringAfter(path, contextPath);
        }
		return path;
	}


}
