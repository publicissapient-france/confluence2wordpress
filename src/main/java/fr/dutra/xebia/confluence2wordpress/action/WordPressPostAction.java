package fr.dutra.xebia.confluence2wordpress.action;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.opensymphony.util.TextUtils;

import fr.dutra.xebia.confluence2wordpress.config.Config;
import fr.dutra.xebia.confluence2wordpress.util.html.HtmlCleanerHelper;

/**
 * @author Alexandre Dutra
 *
 */
public class WordPressPostAction extends ConfluenceActionSupport {

    private static final long serialVersionUID = 1L;

    //injected fields

    private PageManager pageManager;

    private WikiStyleRenderer wikiStyleRenderer;

    // helpers

    private HtmlCleanerHelper helper = new HtmlCleanerHelper();

    // internal state

    private Page page;

    private String wordPressHtml;

    //form fields

    protected Long pageId;

    private String ignoreConfluenceMacros = "info warning";

    private String uploadedFilesBaseUrl = Config.defaultUploadedFilesBaseUrl();

    private boolean includeRDPHeader = true;

    private PageContext pageContext;

    private String confluenceHtml;

    @Override
    public String execute() throws Exception {
        if(pageId != null) {
            page = pageManager.getPage(pageId);
            pageContext = page.toPageContext();
            confluenceHtml = wikiStyleRenderer.convertWikiToXHtml(pageContext, page.getContent());
            wordPressHtml = helper.clean(confluenceHtml, uploadedFilesBaseUrl, includeRDPHeader);
        }
        return SUCCESS;
    }

    public Page getPage() {
        return page;
    }

    public PageContext getPageContext() {
        return pageContext;
    }

    public String getIgnoreConfluenceMacros() {
        return ignoreConfluenceMacros;
    }

    public String getUploadedFilesBaseUrl() {
        return uploadedFilesBaseUrl;
    }

    public boolean isIncludeRDPHeader() {
        return includeRDPHeader;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setWikiStyleRenderer( WikiStyleRenderer wikiStyleRenderer ) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public String getWikiMarkup() {
        return page.getContent();
    }

    public String getWikiMarkupEscaped() {
        return TextUtils.htmlEncode(getWikiMarkup());
    }

    public String getWordPressHtmlMarkup() {
        return wordPressHtml;
    }

    public String getWordPressHtmlMarkupEscaped() {
        return TextUtils.htmlEncode(getWordPressHtmlMarkup());
    }

    public String getConfluenceHtmlMarkup() {
        return confluenceHtml;
    }

    public String getHtmlMarkupEscaped() {
        return TextUtils.htmlEncode(getConfluenceHtmlMarkup());
    }

}