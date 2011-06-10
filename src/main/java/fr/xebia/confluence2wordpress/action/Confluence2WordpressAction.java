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
package fr.xebia.confluence2wordpress.action;

import java.io.IOException;
import java.net.MalformedURLException;
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

import fr.xebia.confluence2wordpress.core.Converter;
import fr.xebia.confluence2wordpress.core.ConverterOptions;
import fr.xebia.confluence2wordpress.core.PageLocator;
import fr.xebia.confluence2wordpress.wp.WordpressClient;
import fr.xebia.confluence2wordpress.wp.WordpressConnection;
import fr.xebia.confluence2wordpress.wp.WordpressPost;

/**
 * @author Alexandre Dutra
 *
 */
public class Confluence2WordpressAction extends ConfluenceActionSupport {

    private static final long serialVersionUID = 1L;

    //Settings

    private String wordpressXmlRpcUrl = "http://localhost/wordpress/xmlrpc.php";

    private String wordpressUserName = "admin";

    private String wordpressPassword = "admin";

    private String wordpressBlogId = "1";

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

    private boolean addRDPHeader;

    private PageContext pageContext;

    @Override
    public String execute() throws Exception {

        processPage();

        if(page != null) {

            convertToHtml();

            handleAction();

            return SUCCESS;
        }

        return ERROR;
    }

    protected void processPage() {
        if(pageId != null) {
            page = pageManager.getPage(pageId);
        } else if (pageUrl != null) {
            page = pageLocator.findPageByIdOrUrl(pageUrl);
        }
    }

    protected void convertToHtml() {
        ConverterOptions options = new ConverterOptions();
        options.setBaseUrl(baseUrl);
        options.setDisableConfluenceMacros(getIgnoreConfluenceMacrosAsList());
        options.setAddRDPHeader(addRDPHeader);
        html = converter.convert(page, options);
    }

    protected void handleAction() throws IOException, XmlRpcException {
        if(actionId != null) {
            if("POST".equals(actionId)) {
                doPost();
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected List<String> getIgnoreConfluenceMacrosAsList() {
        return new StrTokenizer(this.ignoreConfluenceMacros).getTokenList();
    }

    protected WordpressClient newWordpressClient() throws MalformedURLException {
        WordpressConnection wordpressConnection = new WordpressConnection(
            new URL(this.wordpressXmlRpcUrl), this.wordpressUserName, this.wordpressPassword, this.wordpressBlogId);
        WordpressClient client = new WordpressClient(wordpressConnection);
        return client;
    }

    protected void doPost() throws IOException, XmlRpcException {
        WordpressClient client = newWordpressClient();
        WordpressPost post = new WordpressPost();
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

    public boolean isAddRDPHeader() {
        return addRDPHeader;
    }

    public void setAddRDPHeader(boolean addRDPHeader) {
        this.addRDPHeader = addRDPHeader;
    }

}