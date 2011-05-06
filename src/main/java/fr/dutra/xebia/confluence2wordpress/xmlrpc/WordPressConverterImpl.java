package fr.dutra.xebia.confluence2wordpress.xmlrpc;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.rpc.AuthenticationFailedException;
import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.renderer.WikiStyleRenderer;

import fr.dutra.xebia.confluence2wordpress.util.PageRetriever;
import fr.dutra.xebia.confluence2wordpress.util.html.HtmlCleanerHelper;

/**
 * @author Alexandre Dutra
 *
 */
public class WordPressConverterImpl implements WordPressConverter {

    private WikiStyleRenderer wikiStyleRenderer;

    private PageRetriever pageRetriever;

    private HtmlCleanerHelper helper = new HtmlCleanerHelper();

    public String login(String username, String password) throws AuthenticationFailedException, RemoteException {
        return null;
    }

    public boolean logout(String token) throws RemoteException {
        return false;
    }

    /**
     * @inheritdoc
     */
    public String renderContent(String token, String pageUrl, String uploadedFilesBaseUrl) throws Exception {
        Page page = pageRetriever.getPageFromUrl(pageUrl);
        PageContext pageContext = page.toPageContext();
        String confluenceHtml = wikiStyleRenderer.convertWikiToXHtml(pageContext, page.getContent());
        String wordPressHtml = helper.clean(confluenceHtml, uploadedFilesBaseUrl, false);
        return wordPressHtml;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageRetriever = new PageRetriever(pageManager);
    }

    public void setWikiStyleRenderer( WikiStyleRenderer wikiStyleRenderer ) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

}