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
package fr.xebia.confluence2wordpress.core;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.util.GeneralUtil;

public class PageLocator {

    private PageManager pageManager;

    public PageLocator(PageManager pageManager) {
        super();
        this.pageManager = pageManager;
    }

    public Page findPageByIdOrUrl(String idOrUrl) {
        if(StringUtils.isNumeric(idOrUrl)) {
            long pageId = Long.parseLong(idOrUrl);
            return pageManager.getPage(pageId);
        } else if (idOrUrl.contains("/display/")) {
            String spaceKey = GeneralUtil.urlDecode(StringUtils.substringBetween(idOrUrl, "/display/", "/"));
            String title = GeneralUtil.urlDecode(StringUtils.substringAfterLast(idOrUrl, "/"));
            return pageManager.getPage(spaceKey, title);
        } else if (idOrUrl.contains("/pages/viewpage.action?pageId=")) {
            long pageId = Long.parseLong(StringUtils.substringAfter(idOrUrl, "/pages/viewpage.action?pageId="));
            return pageManager.getPage(pageId);
        }
        throw new IllegalArgumentException("Unknown URL: " + idOrUrl);
    }
}
