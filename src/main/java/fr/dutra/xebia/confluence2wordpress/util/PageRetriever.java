package fr.dutra.xebia.confluence2wordpress.util;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.util.GeneralUtil;

public class PageRetriever {

    private PageManager pageManager;

    public PageRetriever(PageManager pageManager) {
        super();
        this.pageManager = pageManager;
    }

    public Page getPageFromUrl(String url) {
        if (url.contains("/display/")) {
            String spaceKey = GeneralUtil.urlDecode(StringUtils.substringBetween(url, "/display/", "/"));
            String title = GeneralUtil.urlDecode(StringUtils.substringAfterLast(url, "/"));
            return pageManager.getPage(spaceKey, title);
        } else if (url.contains("/pages/viewpage.action?pageId=")) {
            long pageId = Long.parseLong(StringUtils.substringAfter(url, "/pages/viewpage.action?pageId="));
            return pageManager.getPage(pageId);
        }
        throw new IllegalArgumentException("Unknown URL: " + url);
    }
}
