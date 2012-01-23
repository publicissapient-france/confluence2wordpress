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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.renderer.MacroManager;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.xwork.ParameterSafe;
import com.opensymphony.util.TextUtils;

import fr.xebia.confluence2wordpress.core.converter.Converter;
import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;
import fr.xebia.confluence2wordpress.core.labels.PageLabelsSynchronizer;
import fr.xebia.confluence2wordpress.core.messages.ActionMessagesManager;
import fr.xebia.confluence2wordpress.core.metadata.Metadata;
import fr.xebia.confluence2wordpress.core.metadata.MetadataException;
import fr.xebia.confluence2wordpress.core.metadata.MetadataManager;
import fr.xebia.confluence2wordpress.core.permissions.PluginPermissionsManager;
import fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager;
import fr.xebia.confluence2wordpress.util.CollectionUtils;
import fr.xebia.confluence2wordpress.wp.WordpressCategory;
import fr.xebia.confluence2wordpress.wp.WordpressClient;
import fr.xebia.confluence2wordpress.wp.WordpressClientFactory;
import fr.xebia.confluence2wordpress.wp.WordpressFile;
import fr.xebia.confluence2wordpress.wp.WordpressPost;
import fr.xebia.confluence2wordpress.wp.WordpressTag;
import fr.xebia.confluence2wordpress.wp.WordpressUser;
import fr.xebia.confluence2wordpress.wp.WordpressXmlRpcException;

/**
 * @author Alexandre Dutra
 *
 */
public class SyncAction extends AbstractPageAwareAction {

	private static final long serialVersionUID = 140791345328730095L;

    private static final String MSG_UPDATE_SUCCESS_KEY = "sync.msg.update.success";

    private static final String MSG_CREATION_SUCCESS_KEY = "sync.msg.creation.success";

    private static final String ERRORS_POST_SLUG_SYNTAX_KEY = "sync.errors.postSlug.syntax";

    private static final String ERRORS_DIGEST_CONCURRENT_MODIFICATION_KEY = "sync.errors.digest.concurrentModification";

    private static final String ERRORS_POST_SLUG_AVAILABILITY_KEY = "sync.errors.postSlug.availability";

    private static final String ERRORS_CONNECTION_FAILED_KEY = "sync.errors.connection.failed";

    private static final String ERRORS_PAGE_TITLE_EMPTY_KEY = "sync.errors.pageTitle.empty";

    private static final String ERRORS_DATE_CREATED_KEY = "sync.errors.dateCreated.invalid";

    private static final String ERRORS_AUTHOR_ID_EMPTY_KEY = "sync.errors.authorId.empty";

    private static final String ERRORS_CATEGORIES_EMPTY_KEY = "sync.errors.categoryNames.empty";
    
    private static final String JS_DATEPICKER_FORMAT_KEY = "sync.js.datepicker.format";

    private static final String WP_TAGS_KEY = "C2W_WP_TAGS";

    private static final String WP_CATEGORIES_KEY = "C2W_WP_CATEGORIES";

    private static final String WP_USERS_KEY = "C2W_WP_USERS";
    
    private static final String MACROS_KEY = "C2W_MACROS";

    private Converter converter;

    private PageManager pageManager;

    private AttachmentManager attachmentManager;

    private PluginPermissionsManager pluginPermissionsManager;

    private PluginSettingsManager pluginSettingsManager;
    
    private WikiStyleRenderer wikiStyleRenderer;
    
    private MacroManager macroManager;
    
    private final MetadataManager metadataManager = new MetadataManager();

    private Metadata metadata;

    private String html;

    private PageLabelsSynchronizer pageLabelsSynchronizer;
    
    private boolean allowPostOverride = false;

    private WordpressClientFactory wordpressClientFactory = new WordpressClientFactory();

    private ActionMessagesManager actionMessagesManager = new ActionMessagesManager();
    
    private String dateCreated;
    
    private String tagNamesAsString;

    private String ignoredConfluenceMacrosAsString;
    
    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setMacroManager(MacroManager macroManager) {
        this.macroManager = macroManager;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
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

    public boolean isRemoteUserHasConfigurationPermission(){
        return pluginPermissionsManager.checkConfigurationPermission(getRemoteUser());
    }
    
    private Converter getConverter(){
        if(converter == null){
            converter = new Converter(wikiStyleRenderer, macroManager);
        }
        return converter;
    }
    
    @SuppressWarnings("unchecked")
    public Set<WordpressUser> getWordpressUsers() {
        return (Set<WordpressUser>) getSession().get(WP_USERS_KEY);
    }

    @SuppressWarnings("unchecked")
    private void setWordpressUsers(Set<WordpressUser> users) {
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

    @SuppressWarnings("unchecked")
    public Set<String> getAvailableMacros(){
        return (Set<String>) getSession().get(MACROS_KEY);
    }
    
    @SuppressWarnings("unchecked")
    private void setAvailableMacros(Set<String> macros) {
        getSession().put(MACROS_KEY, macros);
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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTagNamesAsString() {
        return tagNamesAsString;
    }

    public void setTagNamesAsString(String tagNamesAsString) {
        this.tagNamesAsString = tagNamesAsString;
    }

    public String getIgnoredConfluenceMacrosAsString() {
        return ignoredConfluenceMacrosAsString;
    }

    public void setIgnoredConfluenceMacrosAsString(String ignoredConfluenceMacrosAsString) {
        this.ignoredConfluenceMacrosAsString = ignoredConfluenceMacrosAsString;
    }

    @ParameterSafe
    public Metadata getMetadata() {
        if(metadata == null){
            metadata = new Metadata();
        }
        return metadata;
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
        MessageFormat.format(pluginSettingsManager.getWordpressEditPostRelativePath(), metadata.getPostId().toString());
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
            if (getMetadata().getAuthorId() == null) {
                addActionError(getText(ERRORS_AUTHOR_ID_EMPTY_KEY));
            }
            if (getMetadata().getDigest() != null && ! isAllowPostOverride()) {
                checkConcurrentPostModification();
            }
            
            if(StringUtils.isNotBlank(dateCreated)){
                try {
                    String pattern = getText(JS_DATEPICKER_FORMAT_KEY);
                    Date dateCreated = new SimpleDateFormat(pattern).parse(this.dateCreated);
                    getMetadata().setDateCreated(dateCreated);
                } catch (ParseException e) {
                    addActionError(getText(ERRORS_DATE_CREATED_KEY));
                }
            }
            
            if(getMetadata().getCategoryNames() == null || getMetadata().getCategoryNames().isEmpty()){
                addActionError(getText(ERRORS_CATEGORIES_EMPTY_KEY));
            }
            
            if(StringUtils.isNotBlank(tagNamesAsString)){
                List<String> tagNames = CollectionUtils.split(tagNamesAsString, ",");
                getMetadata().setTagNames(tagNames);
            }
            if(StringUtils.isNotBlank(ignoredConfluenceMacrosAsString)){
                List<String> ignoredConfluenceMacros = CollectionUtils.split(ignoredConfluenceMacrosAsString, ",");
                getMetadata().setIgnoredConfluenceMacros(ignoredConfluenceMacros);
            }
  
        } catch (WordpressXmlRpcException e) {
            addActionError(getText(ERRORS_CONNECTION_FAILED_KEY), e.getMessage());
        }
    }

    private void checkPostSlugAvailability() throws WordpressXmlRpcException {
        WordpressClient client = wordpressClientFactory.newWordpressClient(pluginSettingsManager);
        Integer retrievedPostId = client.findPageIdBySlug(getMetadata().getPostSlug());
        if (retrievedPostId != null && ! retrievedPostId.equals(getMetadata().getPostId())){
            addActionError(getText(ERRORS_POST_SLUG_AVAILABILITY_KEY), retrievedPostId);
        }
    }

    private void checkConcurrentPostModification() throws WordpressXmlRpcException {
        if(getMetadata().getPostId() != null){
            WordpressClient client = wordpressClientFactory.newWordpressClient(pluginSettingsManager);
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
        actionMessagesManager.restoreActionErrorsAndMessagesFromSession(this);
        initSessionElements();
        initMetadata();
        updateFormFields();
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
     * @throws ParseException 
     */
    public String sync() throws IOException, WordpressXmlRpcException, MetadataException, ParseException {
        // consider it a creation if no post ID
        WordpressClient client = wordpressClientFactory.newWordpressClient(pluginSettingsManager);
        boolean creation = this.metadata.getPostId() == null;
        //if we do not know yet the final permalink, we need to do it before the actual conversion
        //from Confluence to Wordpress, because the Converter needs it.
		//in this case we need Wordpress to generate the permalink for us;
        //the easiest way is to post it twice
        if(StringUtils.isEmpty(this.metadata.getPermalink())) {
        	WordpressPost post = metadata.createPost();
        	post.setBody(""); // fake body to lighten xml-rpc request
            post = client.post(post);
            metadata.updateFromPost(post);
        }
        String permalink = metadata.getPermalink();
        WordpressPost post = metadata.createPost();
        post.setBody(createPostBody(false));
        post = client.post(post);
        metadata.updateFromPost(post);
        //Wordpress changed the permalink: we need to reconvert and resync
        //happens only when the post status is changed from draft to published
        if( ! permalink.equals(metadata.getPermalink())) {
        	post = metadata.createPost();
            post.setBody(createPostBody(false));
            post = client.post(post);
            metadata.updateFromPost(post);
        }
        pageLabelsSynchronizer.tagNamesToPageLabels(getPage(), metadata);
        metadataManager.storeMetadata(getPage(), metadata);
        //messages
        if(creation) {
        	addActionMessage(getText(MSG_CREATION_SUCCESS_KEY));
        } else {
        	addActionMessage(getText(MSG_UPDATE_SUCCESS_KEY));
        }
        actionMessagesManager.storeActionErrorsAndMessagesInSession(this);
        return SUCCESS;
    }

    private void initSessionElements() throws WordpressXmlRpcException {
        if(getWordpressUsers() == null) {
            WordpressClient client = wordpressClientFactory.newWordpressClient(pluginSettingsManager);
            Set<WordpressUser> users = new TreeSet<WordpressUser>(new Comparator<WordpressUser>(){
                @Override public int compare(WordpressUser o1, WordpressUser o2) {
                    return new CompareToBuilder().
                        append(StringUtils.lowerCase(o1.getNiceUsername()), StringUtils.lowerCase(o2.getNiceUsername())).
                        append(o1.getId(), o2.getId()).
                        toComparison();
                }
            });
            users.addAll(client.getUsers());
            setWordpressUsers(users);
            Set<WordpressCategory> categories = new TreeSet<WordpressCategory>(new Comparator<WordpressCategory>() {
                @Override public int compare(WordpressCategory o1, WordpressCategory o2) {
                    return new CompareToBuilder().
                    append(StringUtils.lowerCase(o1.getCategoryName()), StringUtils.lowerCase(o2.getCategoryName())).
                    toComparison();
                }
            });
            categories.addAll(client.getCategories());
            setWordpressCategories(categories);
            Set<WordpressTag> tags = new TreeSet<WordpressTag>(new Comparator<WordpressTag>() {
                @Override public int compare(WordpressTag o1, WordpressTag o2) {
                    return new CompareToBuilder().
                    append(StringUtils.lowerCase(o1.getName()), StringUtils.lowerCase(o2.getName())).
                    toComparison();
                }
            });
            tags.addAll(client.getTags()); 
            setWordpressTags(tags);
        }
        if(getAvailableMacros() == null){
            Map<String, Macro> macros = macroManager.getMacros();
            setAvailableMacros(new TreeSet<String>(macros.keySet()));
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

    private void updateFormFields() {
        String pattern = getText(JS_DATEPICKER_FORMAT_KEY);
        if(getMetadata().getDateCreated() != null){
            this.dateCreated = new SimpleDateFormat(pattern).format(getMetadata().getDateCreated());
        }
        this.tagNamesAsString = CollectionUtils.join(getMetadata().getTagNames(), ", ");
        this.ignoredConfluenceMacrosAsString = CollectionUtils.join(getMetadata().getIgnoredConfluenceMacros(), ", ");
    }

    private Map<String, String> uploadAttachments() throws WordpressXmlRpcException, IOException {
        AbstractPage page = getPage();
        Map<String, String> attachmentsMap = new HashMap<String, String>();
        WordpressClient client = wordpressClientFactory.newWordpressClient(pluginSettingsManager);
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
        options.setPageTitle(metadata.getPageTitle());
        options.setPostUrl(metadata.getPermalink());
        options.setIgnoredConfluenceMacros(metadata.getIgnoredConfluenceMacros());
        options.setOptimizeForRDP(metadata.isOptimizeForRDP());
        options.setSyntaxHighlighterPlugin(pluginSettingsManager.getWordpressSyntaxHighlighterPluginAsEnum());
        if( ! preview){
            Map<String, String> attachmentsMap = uploadAttachments();
            options.setAttachmentsMap(attachmentsMap);
        }
        return getConverter().convert(getPage(), options);
    }

}