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
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlrpc.XmlRpcException;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.renderer.WikiStyleRenderer;
import com.opensymphony.util.TextUtils;

import fr.xebia.confluence2wordpress.core.Converter;
import fr.xebia.confluence2wordpress.core.ConverterOptions;
import fr.xebia.confluence2wordpress.form.ConversionForm;
import fr.xebia.confluence2wordpress.wp.WordpressCategory;
import fr.xebia.confluence2wordpress.wp.WordpressClient;
import fr.xebia.confluence2wordpress.wp.WordpressConnection;
import fr.xebia.confluence2wordpress.wp.WordpressFile;
import fr.xebia.confluence2wordpress.wp.WordpressPost;
import fr.xebia.confluence2wordpress.wp.WordpressTag;
import fr.xebia.confluence2wordpress.wp.WordpressUser;

/**
 * @author Alexandre Dutra
 *
 */
public class ConversionAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private static class PostResult implements Serializable {

        private static final long serialVersionUID = 1L;

        private WordpressPost post;

        private boolean creation;

        public PostResult(WordpressPost post, boolean creation) {
            super();
            this.post = post;
            this.creation = creation;
        }

    }

    private Converter converter;

    private PageManager pageManager;

    private AttachmentManager attachmentManager;

    private ConversionForm form = new ConversionForm();

    private String html;

    private boolean displayCreationMessage = false;

    private boolean displayUpdateMessage = false;

    private String permaLink;

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.converter = new Converter(wikiStyleRenderer);
    }

    public Page getPage() {
        return pageManager.getPage(getPageId());
    }

    public Long getPageId() {
        return form.getPageId();
    }

    public void setPageId(Long pageId) {
        form.setPageId(pageId);
    }

    public String getPageTitle() {
        return form.getPageTitle();
    }

    public void setPageTitle(String pageTitle) {
        form.setPageTitle(pageTitle);
    }

    public String getIgnoreConfluenceMacros() {
        return form.getIgnoreConfluenceMacros();
    }

    public List<String> getIgnoreConfluenceMacrosAsList() {
        return form.getIgnoreConfluenceMacrosAsList();
    }

    public void setIgnoreConfluenceMacros(String ignoreConfluenceMacros) {
        form.setIgnoreConfluenceMacros(ignoreConfluenceMacros);
    }

    public String getResourcesBaseUrl() {
        return form.getResourcesBaseUrl();
    }

    public void setResourcesBaseUrl(String resourcesBaseUrl) {
        form.setResourcesBaseUrl(resourcesBaseUrl);
    }

    public Boolean getOptimizeForRDP() {
        return form.getOptimizeForRDP();
    }

    public void setOptimizeForRDP(Boolean optimizeForRDP) {
        form.setOptimizeForRDP(optimizeForRDP);
    }

    public Integer getPostId() {
        return form.getPostId();
    }

    public void setPostId(Integer postId) {
        form.setPostId(postId);
    }

    public String getPostSlug() {
        return form.getPostSlug();
    }

    public void setPostSlug(String postSlug) {
        form.setPostSlug(postSlug);
    }

    public Integer getWordpressUserId() {
        return form.getWordpressUserId();
    }

    public void setWordpressUserId(Integer wordpressUserId) {
        form.setWordpressUserId(wordpressUserId);
    }

    public List<String> getWordpressTagNames() {
        return form.getWordpressTagNames();
    }

    public void setWordpressTagNames(List<String> wordpressTagNames) {
        form.setWordpressTagNames(wordpressTagNames);
    }

    public List<String> getWordpressCategoryNames() {
        return form.getWordpressCategoryNames();
    }

    public void setWordpressCategoryNames(List<String> categoryNames) {
        form.setWordpressCategoryNames(categoryNames);
    }

    public List<WordpressUser> getWordpressUsers() {
        return retrieveWordpressUsers();
    }

    public List<WordpressCategory> getWordpressCategories() {
        return retrieveWordpressCategories();
    }

    public List<WordpressTag> getWordpressTags() {
        return retrieveWordpressTags();
    }

    // read-only

    public String getHtml() {
        return html;
    }

    public String getWiki() {
        return getPage().getContent();
    }

    public String getWikiEscaped() {
        return TextUtils.htmlEncode(getWiki());
    }

    public String getHtmlEscaped() {
        return TextUtils.htmlEncode(getHtml());
    }

    public boolean isDisplayCreationMessage() {
        return displayCreationMessage;
    }

    public boolean isDisplayUpdateMessage() {
        return displayUpdateMessage;
    }

    public String getPermaLink() {
        return permaLink;
    }

    public String getEditLink() {
        return MessageFormat.format(getDefaultWordpressEditPostUrl(), getPostId());
    }

    @Override
    public void validate() {
        super.validate();
    }

    /**
     * Action when the conversion form is displayed.
     * @return The action result.
     * @throws Exception
     */
    public String input() throws Exception {
        ConversionForm sessionForm = retrieveConversionForm();
        if(sessionForm == null) {
            return initConversionForm();
        } else {
            return processFormAfterPost(sessionForm);
        }
    }

    /**
     * Action when a preview is requested.
     * @return The action result.
     * @throws Exception
     */
    public String preview() throws Exception {
        this.html = convert(null);
        return SUCCESS;
    }

    /**
     * Action when a post to Wordpress is to be performed.
     * @return The action result.
     * @throws Exception
     */
    public String post() throws Exception {
        boolean creation = this.getPostId() == null;
        WordpressPost post = new WordpressPost();
        post.setDraft(true);
        post.setPostId(this.getPostId());
        post.setAuthorId(this.getWordpressUserId());
        post.setTitle(this.getPageTitle());
        Map<String, String> attachmentsMap = uploadAttachments();
        post.setBody(convert(attachmentsMap));
        post.setPostSlug(this.getPostSlug());
        post.setCategoryNames(getWordpressCategoryNames()); //categories must exist.
        post.setTagNames(this.getWordpressTagNames()); //tags are dynamically created.
        WordpressClient client = newWordpressClient();
        post = client.post(post);
        PostResult postResult = new PostResult(post, creation);
        storePostResult(postResult);
        storeConversionForm(this.form);
        return SUCCESS;
    }

    public Map<String, String> uploadAttachments() throws IOException, XmlRpcException {
        Page page = getPage();
        Map<String, String> attachmentsMap = new HashMap<String, String>();
        WordpressClient client = newWordpressClient();
        List<Attachment> attachments = attachmentManager.getAttachments(page);
        for (Attachment attachment : attachments) {
            byte[] data = IOUtils.toByteArray(attachment.getContentsAsStream());
            WordpressFile file = new WordpressFile(
                attachment.getFileName(),
                attachment.getContentType(),
                data);
            file = client.uploadFile(file);
            attachmentsMap.put(attachment.getDownloadPath(), file.getUrl());
        }
        return attachmentsMap;
    }

    private String initConversionForm() throws XmlRpcException, IOException {
        if(getPageTitle() == null) {
            String pageTitle = getPage().getTitle();
            if(StringUtils.contains(pageTitle, "DRAFT - ")) {
                setPageTitle(StringUtils.substringAfter(pageTitle, "DRAFT - "));
            } else {
                setPageTitle(pageTitle);
            }
        }
        if(getOptimizeForRDP() == null) {
            if(getPageTitle().contains("Revue de presse")) {
                setOptimizeForRDP(true);
                setPageTitle("Revue de Presse Xebia");
                initWordpressElements();
                List<WordpressCategory> wordpressCategories = getWordpressCategories();
                for (WordpressCategory wordpressCategory : wordpressCategories) {
                    if(StringUtils.containsIgnoreCase(wordpressCategory.getCategoryName(), "revue de presse")){
                        getWordpressCategoryNames().add(wordpressCategory.getCategoryName());
                    }
                }
                Calendar now = Calendar.getInstance();
                setPostSlug(String.format("revue-de-presse-xebia-%1$tY-%2$02d", now.getTime(), now.get(Calendar.WEEK_OF_YEAR)));
                for (WordpressUser wordpressUser : getWordpressUsers()) {
                    if("xebia-france".equals(wordpressUser.getDisplayName())){
                        setWordpressUserId(wordpressUser.getId());
                        break;
                    }
                }
            }
        }
        if(getWordpressUserId() == null) {
            String creatorName = getPage().getCreatorName();
            if(creatorName != null) {
                for (WordpressUser wordpressUser : getWordpressUsers()) {
                    if(creatorName.equals(wordpressUser.getLogin())){
                        setWordpressUserId(wordpressUser.getId());
                        break;
                    }
                }
            }
        }
        if(getResourcesBaseUrl() == null) {
            setResourcesBaseUrl(getDefaultResourcesBaseUrlFormatted());
        }
        if(getIgnoreConfluenceMacros() == null) {
            setIgnoreConfluenceMacros(getDefaultIgnoreConfluenceMacros());
        }
        return SUCCESS;
    }

    private String processFormAfterPost(ConversionForm sessionForm) {
        this.form = sessionForm;
        storeConversionForm(null);
        PostResult result = retrievePostResult();
        storePostResult(null);
        setPostId(result.post.getPostId());
        setWordpressUserId(result.post.getAuthorId());
        setPageTitle(result.post.getTitle());
        setWordpressCategoryNames(result.post.getCategoryNames());
        setWordpressTagNames(result.post.getTagNames());
        setPostSlug(result.post.getPostSlug());
        this.permaLink = result.post.getLink();
        this.displayCreationMessage = result.creation;
        this.displayUpdateMessage = ! result.creation;
        return SUCCESS;
    }

    private String convert(Map<String, String> attachmentsMap) {
        ConverterOptions options = new ConverterOptions();
        options.setResourcesBaseUrl(getResourcesBaseUrl());
        options.setDisableConfluenceMacros(getIgnoreConfluenceMacrosAsList());
        options.setOptimizeForRDP(getOptimizeForRDP() == null ? false : getOptimizeForRDP());
        options.setAttachmentsMap(attachmentsMap);
        Page page = getPage();
        String originalTitle = page.getTitle();
        try {
            page.setTitle(getPageTitle());
            return converter.convert(page, options);
        } finally {
            page.setTitle(originalTitle);
        }
    }

    private synchronized void initWordpressElements() throws XmlRpcException, IOException {
        if(getWordpressUsers() == null) {
            WordpressClient client = newWordpressClient();
            storeWordpressUsers(client.getUsers());
            storeWordpressCategories(client.getCategories());
            storeWordpressTags(client.getTags());
        }
    }

    private WordpressClient newWordpressClient() throws MalformedURLException {
        WordpressConnection wordpressConnection = new WordpressConnection(
            new URL(getDefaultWordpressXmlRpcUrl()),
            getDefaultWordpressUserName(),
            getDefaultWordpressPassword(),
            getDefaultWordpressBlogId());
        return new WordpressClient(wordpressConnection);
    }

    @SuppressWarnings("unchecked")
    private void storeConversionForm(ConversionForm form) {
        getSession().put("C2W_CONVERSION_FORM", form);
    }

    private ConversionForm retrieveConversionForm() {
        return (ConversionForm) getSession().get("C2W_CONVERSION_FORM");
    }

    @SuppressWarnings("unchecked")
    private void storePostResult(PostResult result) {
        getSession().put("C2W_POST_RESULT", result);
    }

    private PostResult retrievePostResult() {
        return (PostResult) getSession().get("C2W_POST_RESULT");
    }

    @SuppressWarnings("unchecked")
    private void storeWordpressUsers(List<WordpressUser> users) {
        getSession().put("C2W_WP_USERS", users);
    }

    @SuppressWarnings("unchecked")
    private List<WordpressUser> retrieveWordpressUsers() {
        return (List<WordpressUser>) getSession().get("C2W_WP_USERS");
    }

    @SuppressWarnings("unchecked")
    private void storeWordpressCategories(List<WordpressCategory> categories) {
        getSession().put("C2W_WP_CATEGORIES", categories);
    }

    @SuppressWarnings("unchecked")
    private List<WordpressCategory> retrieveWordpressCategories() {
        return (List<WordpressCategory>) getSession().get("C2W_WP_CATEGORIES");
    }

    @SuppressWarnings("unchecked")
    private void storeWordpressTags(List<WordpressTag> tags) {
        getSession().put("C2W_WP_TAGS", tags);
    }

    @SuppressWarnings("unchecked")
    private List<WordpressTag> retrieveWordpressTags() {
        return (List<WordpressTag>) getSession().get("C2W_WP_TAGS");
    }

}