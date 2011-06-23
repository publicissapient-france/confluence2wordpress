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

import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;
import org.htmlcleaner.Utils;


/**
 * @author Alexandre Dutra
 *
 */
public class UrlConverter implements TagNodeVisitor {

    private String resourcesBaseUrl;


    public UrlConverter(String resourcesBaseUrl) {
        super();
        this.resourcesBaseUrl = resourcesBaseUrl;
    }


    public String getResourcesBaseUrl() {
        return resourcesBaseUrl;
    }


    public void setResourcesBaseUrl(String resourcesBaseUrl) {
        this.resourcesBaseUrl = resourcesBaseUrl;
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
                if (src != null) {
                    tag.setAttribute("src", Utils.fullUrl(resourcesBaseUrl, src));
                }
            }
        }
        return true;
    }

}
