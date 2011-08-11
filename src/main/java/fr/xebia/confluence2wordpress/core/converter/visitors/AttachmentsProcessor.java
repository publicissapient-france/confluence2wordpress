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

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringEscapeUtils;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;


/**
 * @author Alexandre Dutra
 *
 */
public class AttachmentsProcessor implements TagNodeVisitor {

    private Map<String, String> attachmentsMap;


    public AttachmentsProcessor(Map<String, String> attachmentsMap) {
        super();
        this.attachmentsMap = attachmentsMap;
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
                //URL is absolute, i.e. starts with scheme "http://" but has no port number :(
                if (url != null) {
                    String src = replaceAttachmentUrl(url);
                    if(src != null) {
                        tag.setAttribute("src", src);
                    }
                }
            } else if ("a".equals(tagName)) {
                String url = tag.getAttributeByName("href");
                // URL starts with context path, i.e. "/confluence"
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
        //url may contain "&amp;" - due to htmlcleaner?
        String sanitized = StringEscapeUtils.unescapeXml(url);
        for (Entry<String,String> entry : attachmentsMap.entrySet()) {
            if(sanitized.endsWith(entry.getKey())){
                return entry.getValue();
            }
        }
        return null;
    }


}
