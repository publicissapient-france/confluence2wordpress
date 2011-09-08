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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.xwork.ParameterSafe;
import com.opensymphony.util.TextUtils;

import fr.xebia.confluence2wordpress.core.converter.Converter;
import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;
import fr.xebia.confluence2wordpress.core.labels.PageLabelsSynchronizer;
import fr.xebia.confluence2wordpress.core.metadata.Metadata;
import fr.xebia.confluence2wordpress.core.metadata.MetadataException;
import fr.xebia.confluence2wordpress.core.metadata.MetadataManager;
import fr.xebia.confluence2wordpress.core.permissions.PluginPermissionsManager;
import fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager;
import fr.xebia.confluence2wordpress.wp.WordpressCategory;
import fr.xebia.confluence2wordpress.wp.WordpressClient;
import fr.xebia.confluence2wordpress.wp.WordpressFile;
import fr.xebia.confluence2wordpress.wp.WordpressPost;
import fr.xebia.confluence2wordpress.wp.WordpressTag;
import fr.xebia.confluence2wordpress.wp.WordpressUser;
import fr.xebia.confluence2wordpress.wp.WordpressXmlRpcException;

/**
 * @author Alexandre Dutra
 *
 */
public class ConversionAction extends AbstractPageAwareAction {

    private static final long serialVersionUID = 140791345328730095L;

    private static final String MSG_UPDATE_SUCCESS_KEY = "convert.msg.update.success";

    private static final String MSG_CREATION_SUCCESS_KEY = "convert.msg.creation.success";

    private static final String ERRORS_POST_SLUG_SYNTAX_KEY = "convert.errors.postSlug.syntax";

    private static final String ERRORS_DIGEST_CONCURRENT_MODIFICATION_KEY = "convert.errors.digest.concurrentModification";

    private static final String ERRORS_POST_SLUG_AVAILABILITY_KEY = "convert.errors.postSlug.availability";

    private static final String ERRORS_CONNECTION_FAILED_KEY = "convert.errors.connection.failed";

    private static final String ERRORS_PAGE_TITLE_EMPTY_KEY = "convert.errors.pageTitle.empty";

    private static final String WP_TAGS_KEY = "C2W_WP_TAGS";

    private static final String WP_CATEGORIES_KEY = "C2W_WP_CATEGORIES";

    private static final String WP_USERS_KEY = "C2W_WP_USERS";

    private static final String ACTION_ERRORS_KEY = "C2W_ACTION_ERRORS";
    
    private static final String ACTION_MESSAGES_KEY = "C2W_ACTION_MESSAGES";
    
    private Converter converter;

    private PageManager pageManager;

    private AttachmentManager attachmentManager;

    private PluginPermissionsManager pluginPermissionsManager;

    private PluginSettingsManager pluginSettingsManager;
    
    private UserManager userManager;

    private final MetadataManager metadataManager = new MetadataManager();

    private Metadata metadata;

    private String html;

    private PageLabelsSynchronizer pageLabelsSynchronizer;
    
    private boolean allowPostOverride = false;


    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.converter = new Converter(wikiStyleRenderer);
    }

    public void setPluginSettingsManager(PluginSettingsManager pluginSettingsManager) {
        this.pluginSettingsManager = pluginSettingsManager;
    }
    
    public void setPluginPermissionManager(PluginPermissionsManager pluginPermissionsManager) {
        this.pluginPermissionsManager = pluginPermissionsManager;
    }

    public void setPageLabelsSynchronizer(PageLabelsSynchronizer pageLabelsSynchronizer) {
        this.pageLabelsSynchronizer = pageLabelsSynchronizer;
    }

    public void setSalUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public boolean isRemoteUserAdmin(){
        return userManager.isAdmin(getRemoteUser().getName());
    }
    
    @SuppressWarnings("unchecked")
    public List<WordpressUser> getWordpressUsers() {
        return (List<WordpressUser>) getSession().get(WP_USERS_KEY);
    }

    @SuppressWarnings("unchecked")
    private void setWordpressUsers(List<WordpressUser> users) {
        getSession().put(WP_USERS_KEY, users);
    }

    @SuppressWarnings("unchecked")
    public Set<WordpressCategory> getWordpressCategories() {
        return (Set<WordpressCategory>) getSession().get(WP_CATEGORIES_KEY);
    }

    @SuppressWarnings("unchecked")
    private void setWordpressCategories(Set<WordpressCategory> categories) {
        getSession().put(WP_CATEGORIES_KEY, categories);
    }

    @SuppressWarnings("unchecked")
    public Set<WordpressTag> getWordpressTags() {
        return (Set<WordpressTag>) getSession().get(WP_TAGS_KEY);
    }
    
    @SuppressWarnings("unchecked")
    private void setWordpressTags(Set<WordpressTag> tags) {
        getSession().put(WP_TAGS_KEY, tags);
    }

    public void setPageId(long pageId){
        this.setPage(pageManager.getPage(pageId));
    }

    public boolean isAllowPostOverride() {
        return allowPostOverride;
    }
    
    public void setAllowPostOverride(boolean allowPostOverride) {
        this.allowPostOverride = allowPostOverride;
    }

    @ParameterSafe
    public Metadata getMetadata() {
        if(metadata == null){
            metadata = new Metadata();
        }
        return metadata;
    }

    public String getIgnoredConfluenceMacros() {
    	return StringUtils.join(getMetadata().getIgnoredConfluenceMacros(), ' ');
    }
    
    public void setIgnoredConfluenceMacros(String ignoredConfluenceMacros) {
    	getMetadata().setIgnoredConfluenceMacros(Arrays.asList(StringUtils.split(ignoredConfluenceMacros)));
    }

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

    public String getEditLink() {
        return pluginSettingsManager.getWordpressRootUrl() + 
        MessageFormat.format(pluginSettingsManager.getWordpressEditPostUrl(), metadata.getPostId());
    }

    public String getConfluenceRootUrl(){
        return settingsManager.getGlobalSettings().getBaseUrl();
    }

    @Override
    public boolean isPermitted() {
        return super.isPermitted() && pluginPermissionsManager.checkUsagePermission(getRemoteUser(), getPage());
    }

    @Override
    public void validate() {
        try {
            if (StringUtils.isBlank(getMetadata().getPageTitle())) {
                addActionError(getText(ERRORS_PAGE_TITLE_EMPTY_KEY));
            }
            if (StringUtils.isNotBlank(getMetadata().getPostSlug())) {
                checkPostSlugSyntax();
                checkPostSlugAvailability();
            }
            if (getMetadata().getDigest() != null && ! isAllowPostOverride()) {
                checkConcurrentPostModification();
            }
        } catch (WordpressXmlRpcException e) {
            addActionError(getText(ERRORS_CONNECTION_FAILED_KEY), e.getMessage());
        }
    }

    private void checkPostSlugAvailability() throws WordpressXmlRpcException {
        WordpressClient client = pluginSettingsManager.newWordpressClient();
        Integer retrievedPostId = client.findPageIdBySlug(getMetadata().getPostSlug());
        if (retrievedPostId != null && ! retrievedPostId.equals(getMetadata().getPostId())){
            addActionError(getText(ERRORS_POST_SLUG_AVAILABILITY_KEY), retrievedPostId);
        }
    }

    private void checkConcurrentPostModification() throws WordpressXmlRpcException {
        if(getMetadata().getPostId() != null){
            WordpressClient client = pluginSettingsManager.newWordpressClient();
            WordpressPost post = client.findPostById(getMetadata().getPostId());
            if(post == null || ! StringUtils.equals(post.getDigest(), getMetadata().getDigest())){
                addActionError(getText(ERRORS_DIGEST_CONCURRENT_MODIFICATION_KEY));
            }
        }
    }

    private void checkPostSlugSyntax() {
        if( ! getMetadata().getPostSlug().matches("[a-zA-Z0-9\\-_]+")){
            addActionError(getText(ERRORS_POST_SLUG_SYNTAX_KEY));
        }
    }

    /**
     * Action when the synchronization form is displayed.
     * @return The action result.
     * @throws MetadataException 
     * @throws WordpressXmlRpcException 
     */
    public String input() throws MetadataException, WordpressXmlRpcException {
        restoreActionErrorsAndMessagesFromSession();
        initFormElements();
        initMetadata();
        mergeLocalAndRemoteTags();
        return SUCCESS;
    }

    /**
     * Action when a preview is requested.
     * @return The action result.
     * @throws Exception
     */
    public String preview() throws Exception {
        this.html = createPostBody(true);
        return SUCCESS;
    }

    /**
     * Action when a sync with Wordpress is to be performed.
     * @return The action result.
     * @throws IOException 
     * @throws WordpressXmlRpcException 
     * @throws MetadataException 
     */
    public String sync() throws IOException, WordpressXmlRpcException, MetadataException {
        //create the post
        boolean creation = this.metadata.getPostId() == null;
        WordpressPost post = metadata.createPost(createPostBody(false));
        //post it
        WordpressClient client = pluginSettingsManager.newWordpressClient();
        post = client.post(post);
        //update metadata
        metadata.updateFromPost(post);
        pageLabelsSynchronizer.tagNamesToPageLabels(getPage(), metadata);
        metadataManager.storeMetadata(getPage(), metadata);
        if(creation) addActionMessage(getText(MSG_CREATION_SUCCESS_KEY));
        else addActionMessage(getText(MSG_UPDATE_SUCCESS_KEY));
        storeActionErrorsAndMessagesInSession();
        return SUCCESS;
    }

    @SuppressWarnings("unchecked")
    private void storeActionErrorsAndMessagesInSession() {
        getSession().put(ACTION_ERRORS_KEY, getActionErrors());
        getSession().put(ACTION_MESSAGES_KEY, getActionMessages());
    }
    
    @SuppressWarnings("unchecked")
    private void restoreActionErrorsAndMessagesFromSession() {
        List<String> errors = (List<String>) getSession().get(ACTION_ERRORS_KEY);
        if(errors != null){
            for (String error : errors) {
                addActionError(error);
            }
            getSession().put(ACTION_ERRORS_KEY, null);
        }
        List<String> messages = (List<String>) getSession().get(ACTION_MESSAGES_KEY);
        if(messages != null){
            for (String message : messages) {
                addActionMessage(message);
            }
            getSession().put(ACTION_MESSAGES_KEY, null);
        }
    }
        
    private void initFormElements() throws WordpressXmlRpcException {
        if(getWordpressUsers() == null) {
            WordpressClient client = pluginSettingsManager.newWordpressClient();
            setWordpressUsers(client.getUsers());
            TreeSet<WordpressCategory> categories = new TreeSet<WordpressCategory>(new Comparator<WordpressCategory>() {
                @Override public int compare(WordpressCategory o1, WordpressCategory o2) {
                    return o1.getCategoryName().compareTo(o2.getCategoryName());
                }
            });
            categories.addAll(client.getCategories());
            setWordpressCategories(categories);
            Set<WordpressTag> tags = new TreeSet<WordpressTag>(new Comparator<WordpressTag>() {
                @Override public int compare(WordpressTag o1, WordpressTag o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            tags.addAll(client.getTags()); 
            setWordpressTags(tags);
        }
    }

    private void mergeLocalAndRemoteTags() {
        List<String> tagNames = metadata.getTagNames();
        if(tagNames != null){
            for (String tagName : tagNames) {
                boolean found = false;
                Set<WordpressTag> wordpressTags = getWordpressTags();
                for (WordpressTag tag : wordpressTags) {
                    if(tag.getName().equals(tagName)){
                        found = true;
                        break;
                    }
                }
                if( ! found){
                    wordpressTags.add(new WordpressTag(tagName));
                }
            }
        }
    }

    private void initMetadata() throws MetadataException {
        if(metadata == null){
            metadata = metadataManager.extractMetadata(getPage());
            if(metadata == null) {
                metadata = metadataManager.createMetadata(
                    getPage(), 
                    getWordpressUsers(), 
                    getWordpressCategories(),
                    pluginSettingsManager.getDefaultIgnoredConfluenceMacrosAsList()
                );
            }
        }
        pageLabelsSynchronizer.pageLabelsToTagNames(getPage(), metadata);
        metadataManager.storeMetadata(getPage(), metadata);
    }

    private Map<String, String> uploadAttachments() throws WordpressXmlRpcException, IOException {
        AbstractPage page = getPage();
        Map<String, String> attachmentsMap = new HashMap<String, String>();
        WordpressClient client = pluginSettingsManager.newWordpressClient();
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

    private String createPostBody(boolean preview) throws WordpressXmlRpcException, IOException {
        ConverterOptions options = new ConverterOptions();
        options.setIgnoredConfluenceMacros(metadata.getIgnoredConfluenceMacros());
        options.setOptimizeForRDP(metadata.isOptimizeForRDP());
        options.setSyntaxHighlighterPlugin(pluginSettingsManager.getWordpressSyntaxHighlighterPluginAsEnum());
        if( ! preview){
            Map<String, String> attachmentsMap = uploadAttachments();
            options.setAttachmentsMap(attachmentsMap);
        }
        AbstractPage page = getPage();
        String originalTitle = page.getTitle();
        try {
            page.setTitle(metadata.getPageTitle());
            return converter.convert(page, options);
        } finally {
            page.setTitle(originalTitle);
        }
    }

}