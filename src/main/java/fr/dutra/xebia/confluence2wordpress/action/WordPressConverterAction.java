package fr.dutra.xebia.confluence2wordpress.action;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.renderer.WikiStyleRenderer;
import com.opensymphony.util.TextUtils;

import fr.dutra.xebia.confluence2wordpress.config.Config;
import fr.dutra.xebia.confluence2wordpress.util.html.HtmlCleanerHelper;

/**
 * @author Alexandre Dutra
 *
 */
public class WordPressConverterAction extends ConfluenceActionSupport {

    private static final long serialVersionUID = 1L;

    //injected fields

    private PageManager pageManager;

    private WikiStyleRenderer wikiStyleRenderer;

    // helpers

    private HtmlCleanerHelper helper = new HtmlCleanerHelper();

    // internal state

    private Page page;

    private String html;

    //form fields

    protected Long pageId;

    private String ignoreConfluenceMacros = "info";

    private String uploadedFilesBaseUrl = Config.defaultUploadedFilesBaseUrl();

    private String htmlBaseUrl = Config.defaultRevueDePresseUrl();

    private boolean includeRDPHeader = true;

    private boolean includePictures = true;

    private boolean includeTables = true;

    @Override
    public String execute() throws Exception {
        if(pageId != null) {
            page = pageManager.getPage(pageId);
            String rawHtml = wikiStyleRenderer.convertWikiToXHtml(page.toPageContext(), page.getContent());
            html = helper.clean(rawHtml, htmlBaseUrl, uploadedFilesBaseUrl, includeRDPHeader);
        }
        return SUCCESS;
    }

    public Page getPage() {
        return page;
    }

    public String getIgnoreConfluenceMacros() {
        return ignoreConfluenceMacros;
    }

    public String getUploadedFilesBaseUrl() {
        return uploadedFilesBaseUrl;
    }

    public String getHtmlBaseUrl() {
        return htmlBaseUrl;
    }

    public boolean isIncludeRDPHeader() {
        return includeRDPHeader;
    }

    public boolean isIncludePictures() {
        return includePictures;
    }

    public boolean isIncludeTables() {
        return includeTables;
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

    public String getHtmlMarkup() {
        return html;
    }

    public String getHtmlMarkupEscaped() {
        return TextUtils.htmlEncode(getHtmlMarkup());
    }

}