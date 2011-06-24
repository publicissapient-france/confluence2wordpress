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
package fr.xebia.confluence2wordpress.core.visitors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

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
                String src = tag.getAttributeByName("src");
                //http:// mais sans port :(
                if (src != null) {
                    try {
                        URL url = new URL(src);
                        String urlString = url.getPath();
                        if(url.getQuery() != null) {
                            urlString += url.getQuery();
                        }
                        if(attachmentsMap.containsKey(urlString)) {
                            tag.setAttribute("src", attachmentsMap.get(url));
                        }
                    } catch (MalformedURLException e) {
                    }
                }
            } else if ("a".equals(tagName)) {
                String url = tag.getAttributeByName("href");
                // /confluence
                if (url != null) {
                    if(attachmentsMap.containsKey(url)) {
                        tag.setAttribute("href", attachmentsMap.get(url));
                    }
                }
            }
        }
        return true;
    }


}
