package fr.dutra.xebia.confluence2wordpress.action;

import java.io.IOException;
import java.net.URL;

import org.apache.xmlrpc.XmlRpcException;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.renderer.WikiStyleRenderer;
import com.opensymphony.util.TextUtils;

import fr.dutra.xebia.confluence2wordpress.config.Config;
import fr.dutra.xebia.confluence2wordpress.util.PageRetriever;
import fr.dutra.xebia.confluence2wordpress.util.html.HtmlCleanerHelper;
import fr.dutra.xebia.confluence2wordpress.wp.WordPressClient;
import fr.dutra.xebia.confluence2wordpress.wp.WordPressConnection;
import fr.dutra.xebia.confluence2wordpress.wp.WordPressPost;

/**
 * @author Alexandre Dutra
 *
 */
public class WordPressConverterAction extends ConfluenceActionSupport {

    private static final long serialVersionUID = 1L;

    //injected fields

    private PageManager pageManager;

    private WikiStyleRenderer wikiStyleRenderer;

    private SettingsManager settingsManager;


    // helpers

    private HtmlCleanerHelper helper = new HtmlCleanerHelper();

    private PageRetriever pageRetriever;

    // internal state

    private Page page;

    private String wordPressHtml;

    //form fields

    private String pageUrl;

    private Long pageId;

    private String actionId = "INIT";

    private String ignoreConfluenceMacros = "info warning";

    private String uploadedFilesBaseUrl = Config.defaultUploadedFilesBaseUrl();

    private boolean includeRDPHeader = true;

    private PageContext pageContext;

    private String confluenceHtml;

    @Override
    public String execute() throws Exception {
        if(pageId != null) {
            page = pageManager.getPage(pageId);
        } else if (pageUrl != null) {
            page = pageRetriever.getPageFromUrl(pageUrl);
        }
        if(page != null) {
            pageContext = page.toPageContext();
            confluenceHtml = wikiStyleRenderer.convertWikiToXHtml(pageContext, page.getContent());
            wordPressHtml = helper.clean(confluenceHtml, uploadedFilesBaseUrl, includeRDPHeader);
            if(actionId != null) {
                if("POST".equals(actionId)) {
                    doPost();
                }
            }
            return SUCCESS;
        }
        return ERROR;
    }

    private void doPost() throws IOException, XmlRpcException {
        URL url = new URL("http://localhost/wordpress/xmlrpc.php");
        String userName = "admin";
        String password = "admin";
        String blogId = "1";
        WordPressConnection wordPressConnection = new WordPressConnection(url, userName, password, blogId);
        WordPressClient client = new WordPressClient(wordPressConnection);
        WordPressPost post = new WordPressPost();
        post.setAuthorId(3);
        post.setTitle("Revue de Presse Xebia");
        post.setBody(wordPressHtml);
        post.setCategoryNames("test", "newcat");//categories must exist.
        post.setTagNames("tag1", "tag2", "newtag"); //tags are dynamically created.
        post = client.post(post);
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
        this.pageRetriever = new PageRetriever(pageManager);
    }

    public void setWikiStyleRenderer( WikiStyleRenderer wikiStyleRenderer ) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    @Override
    public void setSettingsManager(SettingsManager settingsManager){
        this.settingsManager = settingsManager;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
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

    public String getBaseUrl() {
        return settingsManager.getGlobalSettings().getBaseUrl();
    }

}