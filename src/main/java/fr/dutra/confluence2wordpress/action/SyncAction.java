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
package fr.dutra.confluence2wordpress.action;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;

import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.xwork.ParameterSafe;
import com.opensymphony.xwork.util.XWorkList;

import fr.dutra.confluence2wordpress.core.converter.ConversionException;
import fr.dutra.confluence2wordpress.core.messages.ActionMessagesManager;
import fr.dutra.confluence2wordpress.core.metadata.Metadata;
import fr.dutra.confluence2wordpress.core.metadata.MetadataException;
import fr.dutra.confluence2wordpress.core.metadata.MetadataManager;
import fr.dutra.confluence2wordpress.core.permissions.PluginPermissionsManager;
import fr.dutra.confluence2wordpress.core.settings.PluginSettingsManager;
import fr.dutra.confluence2wordpress.core.sync.PageLabelsSynchronizer;
import fr.dutra.confluence2wordpress.core.sync.SynchronizationException;
import fr.dutra.confluence2wordpress.core.sync.WordpressSynchronizer;
import fr.dutra.confluence2wordpress.util.CollectionUtils;
import fr.dutra.confluence2wordpress.wp.WordpressCategory;
import fr.dutra.confluence2wordpress.wp.WordpressClient;
import fr.dutra.confluence2wordpress.wp.WordpressPost;
import fr.dutra.confluence2wordpress.wp.WordpressTag;
import fr.dutra.confluence2wordpress.wp.WordpressUser;
import fr.dutra.confluence2wordpress.wp.WordpressXmlRpcException;

/**
 * @author Alexandre Dutra
 *
 */
public class SyncAction extends AbstractPageAwareAction {

	private static final long serialVersionUID = 140791345328730095L;

	// i18n keys
	
    private static final String MSG_UPDATE_SUCCESS_KEY = "sync.msg.update.success";

    private static final String MSG_CREATION_SUCCESS_KEY = "sync.msg.creation.success";

    private static final String ERRORS_POST_SLUG_SYNTAX_KEY = "sync.errors.postSlug.syntax";

    private static final String ERRORS_DIGEST_CONCURRENT_MODIFICATION_KEY = "sync.errors.digest.concurrentModification";

    private static final String ERRORS_POST_SLUG_AVAILABILITY_KEY = "sync.errors.postSlug.availability";

    private static final String ERRORS_PAGE_TITLE_EMPTY_KEY = "sync.errors.pageTitle.empty";

    private static final String ERRORS_DATE_CREATED_KEY = "sync.errors.dateCreated.invalid";

    private static final String ERRORS_AUTHOR_ID_EMPTY_KEY = "sync.errors.authorId.empty";

    private static final String ERRORS_CATEGORIES_EMPTY_KEY = "sync.errors.categoryNames.empty";

	private static final String ERRORS_TAG_NAME_EMPTY_KEY = "sync.errors.tagName.empty";

	private static final String ERRORS_TAG_NAME_INVALID_KEY = "sync.errors.tagName.invalid";

	private static final String ERRORS_TAG_ATTRIBUTE_EMPTY_KEY = "sync.errors.tagAttribute.empty";

    private static final String ERRORS_CONVERSION = "sync.errors.conversion";

    private static final String ERRORS_XMLRPC = "sync.errors.xmlrpc";

    private static final String ERRORS_METADATA = "sync.errors.metadata";

    private static final String ERRORS_SYNC = "sync.errors.sync";

    private static final String JS_DATEPICKER_FORMAT_KEY = "sync.js.datepicker.format";

    // Session keys
    
    private static final String WP_TAGS_KEY = "C2W_WP_TAGS";

    private static final String WP_CATEGORIES_KEY = "C2W_WP_CATEGORIES";

    private static final String WP_USERS_KEY = "C2W_WP_USERS";
    
    private static final String MACROS_KEY = "C2W_MACROS";

	private static final String METADATA_KEY = "C2W_METADATA";

	// validating patterns
	
    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9]+");

	private static final Pattern POST_SLUG_PATTERN = Pattern.compile("[a-zA-Z0-9\\-_]+");

    // injected dependencies
    
    private PageManager pageManager;

    private PluginPermissionsManager pluginPermissionsManager;

    private PluginSettingsManager pluginSettingsManager;
    
    private MacroMetadataManager macroMetadataManager;
    
    private MetadataManager metadataManager;

    private WordpressSynchronizer wordpressSynchronizer;

    private PageLabelsSynchronizer pageLabelsSynchronizer;

    private ActionMessagesManager actionMessagesManager = new ActionMessagesManager();

    // form fields
    
    private String html;

    private boolean allowPostOverride = false;

    private String dateCreated;
    
    private String tagNamesAsString;

    private String ignoredConfluenceMacrosAsString;

    @SuppressWarnings("unchecked")
	private List<String> tagNames = new XWorkList(String.class);
    
    @SuppressWarnings("unchecked")
    private List<String> tagAttributes = new XWorkList(String.class);
    
    
    
    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setMacroMetadataManager(MacroMetadataManager macroMetadataManager) {
        this.macroMetadataManager = macroMetadataManager;
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

	public void setWordpressSynchronizer(WordpressSynchronizer wordpressSynchronizer) {
		this.wordpressSynchronizer = wordpressSynchronizer;
	}

	public void setMetadataManager(MetadataManager metadataManager) {
		this.metadataManager = metadataManager;
	}

	public boolean isRemoteUserHasConfigurationPermission(){
        return pluginPermissionsManager.checkConfigurationPermission(getRemoteUser());
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

	@ParameterSafe
    public Metadata getMetadata() {
        return (Metadata) getSession().get(METADATA_KEY);
    }

	@SuppressWarnings("unchecked")
	public void setMetadata(Metadata metadata) {
    	getSession().put(METADATA_KEY, metadata);
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

    public List<String> getTagNames() {
		return tagNames;
	}

	public void setTagNames(List<String> tagNames) {
		this.tagNames = tagNames;
	}

	public List<String> getTagAttributes() {
		return tagAttributes;
	}

	public void setTagAttributes(List<String> tagAttributes) {
		this.tagAttributes = tagAttributes;
	}

    public String getHtml() {
        return html;
    }

    public String getStorage() {
        return getPage().getBodyAsString();
    }

    public String getEditLink() {
        return pluginSettingsManager.getWordpressRootUrl() + 
        MessageFormat.format(pluginSettingsManager.getWordpressEditPostRelativePath(), getMetadata().getPostId().toString());
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
        if (StringUtils.isBlank(getMetadata().getPageTitle())) {
            addActionError(getText(ERRORS_PAGE_TITLE_EMPTY_KEY));
        }
        if (getMetadata().getAuthorId() == null) {
            addActionError(getText(ERRORS_AUTHOR_ID_EMPTY_KEY));
        }
        try {
            if (StringUtils.isNotBlank(getMetadata().getPostSlug())) {
                checkPostSlugSyntax();
                checkPostSlugAvailability();
            }
            if (getMetadata().getDigest() != null && ! isAllowPostOverride()) {
                checkConcurrentPostModification();
            }
        } catch (WordpressXmlRpcException e) {
            addActionError(getText(ERRORS_XMLRPC), e.getMessage());
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
        for (int i = 0; i < tagNames.size(); i++) {
        	String tagName = tagNames.get(i);
			if(StringUtils.isBlank(tagName)){
				addActionError(getText(ERRORS_TAG_NAME_EMPTY_KEY, new Object[]{i+1}));
			} else if( ! TAG_NAME_PATTERN.matcher(tagName).matches()){
				addActionError(getText(ERRORS_TAG_NAME_INVALID_KEY, new Object[]{tagName, i+1}));
			}
		}
        for (int i = 0; i < tagAttributes.size(); i++) {
        	String tagAttribute = tagAttributes.get(i);
			if(StringUtils.isBlank(tagAttribute)){
				addActionError(getText(ERRORS_TAG_ATTRIBUTE_EMPTY_KEY, new Object[]{i+1}));
			}
		}
        Map<String, String> tagAttributesMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < tagNames.size(); i++) {
        	tagAttributesMap.put(tagNames.get(i), tagAttributes.get(i));
		}
        getMetadata().setTagAttributes(tagAttributesMap);
    }

    private void checkPostSlugAvailability() throws WordpressXmlRpcException {
        WordpressClient client = pluginSettingsManager.getWordpressClient();
        Integer retrievedPostId = client.findPageIdBySlug(getMetadata().getPostSlug());
        if (retrievedPostId != null && ! retrievedPostId.equals(getMetadata().getPostId())){
            addActionError(getText(ERRORS_POST_SLUG_AVAILABILITY_KEY), new Object[]{retrievedPostId});
        }
    }

    private void checkConcurrentPostModification() throws WordpressXmlRpcException {
        if(getMetadata().getPostId() != null){
            WordpressClient client = pluginSettingsManager.getWordpressClient();
            WordpressPost post = client.findPostById(getMetadata().getPostId());
            if(post == null || ! StringUtils.equals(post.getDigest(), getMetadata().getDigest())){
                addActionError(getText(ERRORS_DIGEST_CONCURRENT_MODIFICATION_KEY));
            }
        }
    }

    private void checkPostSlugSyntax() {
        if( ! POST_SLUG_PATTERN.matcher(getMetadata().getPostSlug()).matches()){
            addActionError(getText(ERRORS_POST_SLUG_SYNTAX_KEY));
        }
    }

    /**
     * Action when the synchronization form is displayed.
     * @return The action result.
     */
    public String input() {
        actionMessagesManager.restoreActionErrorsAndMessagesFromSession(this);
        try {
            initSessionElements();
        } catch (WordpressXmlRpcException e) {
            addActionError(ERRORS_XMLRPC, e.getMessage());
        }
        try {
            initMetadata();
        } catch (MetadataException e) {
            addActionError(ERRORS_METADATA, e.getMessage());
        }
        updateFormFields();
        mergeLocalAndRemoteTags();
        return SUCCESS;
    }

    /**
     * Action when a preview is requested.
     * @return The action result.
     */
    public String preview() {
        try {
            this.html = this.wordpressSynchronizer.preview(getPage(), getMetadata());
        } catch (ConversionException e) {
            addActionError(ERRORS_CONVERSION, e.getMessage());
        }
        actionMessagesManager.storeActionErrorsAndMessagesInSession(this);
        return SUCCESS;
    }

    /**
     * Action when a sync with Wordpress is to be performed.
     * @return The action result.
     */
    public String sync() {
        try {
            // consider it a creation if no post ID
            boolean creation = this.getMetadata().getPostId() == null;
            Metadata metadata = this.wordpressSynchronizer.synchronize(getPage(), getMetadata());
			this.setMetadata(metadata);
            this.metadataManager.storeMetadata(getPage(), metadata);
            this.pageLabelsSynchronizer.tagNamesToPageLabels(getPage(), metadata);
            if(creation) {
                addActionMessage(getText(MSG_CREATION_SUCCESS_KEY));
            } else {
                addActionMessage(getText(MSG_UPDATE_SUCCESS_KEY));
            }
        } catch (ConversionException e) {
            addActionError(ERRORS_CONVERSION, e.getMessage());
        } catch (WordpressXmlRpcException e) {
            addActionError(ERRORS_XMLRPC, e.getMessage());
        } catch (SynchronizationException e) {
            addActionError(ERRORS_SYNC, e.getMessage());
        } catch (MetadataException e) {
            addActionError(ERRORS_METADATA, e.getMessage());
        }
        actionMessagesManager.storeActionErrorsAndMessagesInSession(this);
        return SUCCESS;
    }

    private void initSessionElements() throws WordpressXmlRpcException {
        if(getWordpressUsers() == null) {
            WordpressClient client = pluginSettingsManager.getWordpressClient();
            Future<List<WordpressUser>> futureUsers = client.getUsers();
            Future<List<WordpressCategory>> futureCategories = client.getCategories();
            Future<List<WordpressTag>> futureTags = client.getTags();
            Set<WordpressUser> users = new TreeSet<WordpressUser>(new Comparator<WordpressUser>(){
                @Override public int compare(WordpressUser o1, WordpressUser o2) {
                    return new CompareToBuilder().
                        append(StringUtils.lowerCase(o1.getNiceUsername()), StringUtils.lowerCase(o2.getNiceUsername())).
                        append(o1.getId(), o2.getId()).
                        toComparison();
                }
            });
            Set<WordpressCategory> categories = new TreeSet<WordpressCategory>(new Comparator<WordpressCategory>() {
                @Override public int compare(WordpressCategory o1, WordpressCategory o2) {
                    return new CompareToBuilder().
                    append(StringUtils.lowerCase(o1.getCategoryName()), StringUtils.lowerCase(o2.getCategoryName())).
                    toComparison();
                }
            });
            Set<WordpressTag> tags = new TreeSet<WordpressTag>(new Comparator<WordpressTag>() {
                @Override public int compare(WordpressTag o1, WordpressTag o2) {
                    return new CompareToBuilder().
                    append(StringUtils.lowerCase(o1.getName()), StringUtils.lowerCase(o2.getName())).
                    toComparison();
                }
            });
            try {
                users.addAll(futureUsers.get(30, TimeUnit.SECONDS));
                categories.addAll(futureCategories.get(30, TimeUnit.SECONDS));
                tags.addAll(futureTags.get(30, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                throw new WordpressXmlRpcException("Error contacting Wordpress server", e);
            } catch (ExecutionException e) {
                if(e.getCause() instanceof WordpressXmlRpcException){
                    throw (WordpressXmlRpcException) e.getCause();
                }
                throw new WordpressXmlRpcException("Error contacting Wordpress server", e.getCause());
            } catch (TimeoutException e) {
                throw new WordpressXmlRpcException("Connection to Wordpress timed out", e.getCause());
            } 
            setWordpressUsers(users);
            setWordpressCategories(categories);
            setWordpressTags(tags);
        }
        if(getAvailableMacros() == null){
            Set<MacroMetadata> allMacroMetadata = macroMetadataManager.getAllMacroMetadata();
            TreeSet<String> macros = new TreeSet<String>();
            for (MacroMetadata macroMetadata : allMacroMetadata) {
            	macros.add(macroMetadata.getMacroName());
			}
			setAvailableMacros(macros);
        }
    }

    private void mergeLocalAndRemoteTags() {
        Set<WordpressTag> wordpressTags = getWordpressTags();
        if(wordpressTags != null) {
	        List<String> tagNames = getMetadata().getTagNames();
	        if(tagNames != null){
	            for (String tagName : tagNames) {
	                boolean found = false;
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
    }

    private void initMetadata() throws MetadataException {
    	Metadata metadata = metadataManager.extractMetadata(getPage());
        if(metadata == null) {
            metadata = metadataManager.createMetadata(
                getPage(), 
                getWordpressUsers(), 
                getWordpressCategories()
            );
            metadata.setIgnoredConfluenceMacros(pluginSettingsManager.getDefaultIgnoredConfluenceMacrosAsList());
            metadata.setTagAttributes(pluginSettingsManager.getDefaultTagAttributes());
        }
        pageLabelsSynchronizer.pageLabelsToTagNames(getPage(), metadata);
        metadataManager.storeMetadata(getPage(), metadata);
        setMetadata(metadata);
    }

    private void updateFormFields() {
        String pattern = getText(JS_DATEPICKER_FORMAT_KEY);
        if(getMetadata().getDateCreated() != null){
            this.dateCreated = new SimpleDateFormat(pattern).format(getMetadata().getDateCreated());
        }
        this.tagNamesAsString = CollectionUtils.join(getMetadata().getTagNames(), ", ");
        this.ignoredConfluenceMacrosAsString = CollectionUtils.join(getMetadata().getIgnoredConfluenceMacros(), ", ");
        Map<String, String> tagAttributesMap = getMetadata().getTagAttributes();
        if(tagAttributesMap != null) {
	        for (Entry<String, String> entry : tagAttributesMap.entrySet()) {
	        	tagNames.add(entry.getKey());
	        	tagAttributes.add(entry.getValue());
			}
        }
    }

}