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
package fr.dutra.xebia.wiki2html.action;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang.text.StrTokenizer;
import org.apache.xmlrpc.XmlRpcException;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.opensymphony.util.TextUtils;

import fr.dutra.xebia.wiki2html.core.Converter;
import fr.dutra.xebia.wiki2html.core.ConverterOptions;
import fr.dutra.xebia.wiki2html.core.PageLocator;
import fr.dutra.xebia.wiki2html.wp.WordPressClient;
import fr.dutra.xebia.wiki2html.wp.WordPressConnection;
import fr.dutra.xebia.wiki2html.wp.WordPressPost;

/**
 * @author Alexandre Dutra
 *
 */
public class Wiki2HtmlAction extends ConfluenceActionSupport {

    private static final long serialVersionUID = 1L;

    //injected fields

    private PageManager pageManager;

    // helpers

    private PageLocator pageLocator;

    private Converter converter;

    // internal state

    private Page page;

    private String html;

    //form fields

    private String pageUrl;

    private Long pageId;

    private String actionId = "INIT";

    private String ignoreConfluenceMacros = "info warning";

    private String baseUrl;

    private PageContext pageContext;

    @Override
    public String execute() throws Exception {
        if(pageId != null) {
            page = pageManager.getPage(pageId);
        } else if (pageUrl != null) {
            page = pageLocator.findPageByIdOrUrl(pageUrl);
        }
        if(page != null) {
            ConverterOptions options = new ConverterOptions();
            options.setBaseUrl(baseUrl);
            options.setDisableConfluenceMacros(getIgnoreConfluenceMacrosAsList());
            html = converter.convert(page, options);
            if(actionId != null) {
                if("POST".equals(actionId)) {
                    doPost();
                }
            }
            return SUCCESS;
        }
        return ERROR;
    }

    @SuppressWarnings("unchecked")
    private List<String> getIgnoreConfluenceMacrosAsList() {
        return new StrTokenizer(this.ignoreConfluenceMacros).getTokenList();
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
        post.setBody(html);
        post.setCategoryNames("test", "newcat"); //categories must exist.
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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
        this.pageLocator = new PageLocator(pageManager);
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.converter = new Converter(wikiStyleRenderer);
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

    public String getHtmlMarkup() {
        return html;
    }

    public String getHtmlMarkupEscaped() {
        return TextUtils.htmlEncode(getHtmlMarkup());
    }

    public String getConfluenceRootUrl() {
        return settingsManager.getGlobalSettings().getBaseUrl();
    }

}