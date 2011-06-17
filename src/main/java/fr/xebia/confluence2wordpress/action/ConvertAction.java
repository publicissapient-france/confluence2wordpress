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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
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
import fr.xebia.confluence2wordpress.wp.WordpressCategory;
import fr.xebia.confluence2wordpress.wp.WordpressClient;
import fr.xebia.confluence2wordpress.wp.WordpressConnection;
import fr.xebia.confluence2wordpress.wp.WordpressPost;
import fr.xebia.confluence2wordpress.wp.WordpressTag;
import fr.xebia.confluence2wordpress.wp.WordpressUser;

/**
 * @author Alexandre Dutra
 *
 */
public class ConvertAction extends ConfluenceActionSupport {

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

    private String pageTitle;

    private String pageUrl;

    private Long pageId;

    private String actionId = "INIT";

    private String ignoreConfluenceMacros = "info warning";

    private String baseUrl;

    private Boolean optimizeForRDP;

    private Integer wordpressUserId;

    private List<String> wordpressCategoryIds;

    private List<String> wordpressTagNames;

    private PageContext pageContext;

    @Override
    public String execute() throws Exception {

        initWordpressElements();

        processPage();

        if(page != null) {

            preConvert();

            convertToHtml();

            handleAction();

            return SUCCESS;
        }

        return ERROR;
    }

    protected synchronized void initWordpressElements() throws XmlRpcException, IOException {

        @SuppressWarnings("unchecked")
        Map<String,Object> session = getSession();
        WordpressClient client = newWordpressClient();

        if( ! session.containsKey("WP_USERS")) {
            List<WordpressUser> users = client.getUsers();
            CollectionUtils.filter(users, new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    return ((WordpressUser) o).isEditor();
                }
            });
            session.put("WP_USERS", users);
        }

        if( ! session.containsKey("WP_CATEGORIES")) {
            session.put("WP_CATEGORIES", client.getCategories());
        }
        if( ! session.containsKey("WP_TAGS")) {
            session.put("WP_TAGS", client.getTags());
        }
    }

    protected void processPage() {
        if(pageId != null) {
            page = pageManager.getPage(pageId);
        } else if (pageUrl != null) {
            page = pageLocator.findPageByIdOrUrl(pageUrl);
        }
    }

    protected void preConvert() {
        if(pageTitle == null) {
            if(StringUtils.contains(page.getTitle(), "DRAFT - ")) {
                pageTitle = StringUtils.substringAfter(page.getTitle(), "DRAFT - ");
            } else {
                pageTitle = page.getTitle();
            }
        }
        if(optimizeForRDP == null) {
            optimizeForRDP = pageTitle.contains("Revue de presse");
        }
        if(optimizeForRDP) {
            List<WordpressCategory> wordpressCategories = getWordpressCategories();
            for (WordpressCategory wordpressCategory : wordpressCategories) {
                if(StringUtils.containsIgnoreCase(wordpressCategory.getCategoryName(), "revue de presse")){
                    wordpressCategoryIds.add(wordpressCategory.getId().toString());
                }
            }
        }
        if(baseUrl == null) {
            baseUrl = String.format("http://blog.xebia.fr/wp-content/uploads/%1$tY/%1$tm/", new Date());
        }
    }

    protected void convertToHtml() {
        ConverterOptions options = new ConverterOptions();
        options.setBaseUrl(baseUrl);
        options.setDisableConfluenceMacros(getIgnoreConfluenceMacrosAsList());
        options.setOptimizeForRDP(optimizeForRDP);
        String originalTitle = page.getTitle();
        try {
            page.setTitle(pageTitle);
            html = converter.convert(page, options);
        } finally {
            page.setTitle(originalTitle);
        }
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
        post.setAuthorId(this.getWordpressUserId());
        post.setTitle(this.getPageTitle());
        post.setBody(html);
        if(this.wordpressCategoryIds != null && ! this.wordpressCategoryIds.isEmpty()) {
            List<String> categoryNames = new ArrayList<String>();
            for(String categoryId: wordpressCategoryIds) {
                for(WordpressCategory category: getWordpressCategories()) {
                    if(category.getId().equals(Integer.valueOf(categoryId))) {
                        categoryNames.add(category.getCategoryName());
                        break;
                    }
                }
            }
            post.setCategoryNames(categoryNames); //categories must exist.
        }
        if(this.wordpressTagNames != null && ! this.wordpressTagNames.isEmpty()) {
            post.setTagNames(this.wordpressTagNames); //tags are dynamically created.
        }
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

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
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

    public Boolean getOptimizeForRDP() {
        return optimizeForRDP;
    }

    public void setOptimizeForRDP(Boolean optimizeForRDP) {
        this.optimizeForRDP = optimizeForRDP;
    }

    public Integer getWordpressUserId() {
        return wordpressUserId;
    }

    public void setWordpressUserId(Integer wordpressUserId) {
        this.wordpressUserId = wordpressUserId;
    }

    public List<String> getWordpressCategoryIds() {
        return wordpressCategoryIds;
    }

    public void setWordpressCategoryIds(List<String> wordpressCategoryIds) {
        this.wordpressCategoryIds = wordpressCategoryIds;
    }

    public List<String> getWordpressTagNames() {
        return wordpressTagNames;
    }

    public void setWordpressTagNames(List<String> wordpressTagNames) {
        this.wordpressTagNames = wordpressTagNames;
    }

    @SuppressWarnings("unchecked")
    public List<WordpressUser> getWordpressUsers() {
        return (List<WordpressUser>) getSession().get("WP_USERS");
    }

    @SuppressWarnings("unchecked")
    public List<WordpressCategory> getWordpressCategories() {
        return (List<WordpressCategory>) getSession().get("WP_CATEGORIES");
    }

    @SuppressWarnings("unchecked")
    public List<WordpressTag> getWordpressTags() {
        return (List<WordpressTag>) getSession().get("WP_TAGS");
    }

}