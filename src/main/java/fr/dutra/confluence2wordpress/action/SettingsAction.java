/**
 * Copyright 2011-2012 Alexandre Dutra
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.renderer.MacroManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.user.EntityException;
import com.atlassian.user.Group;
import com.atlassian.user.GroupManager;
import com.atlassian.user.search.page.Pager;
import com.opensymphony.xwork.util.XWorkList;

import fr.dutra.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;
import fr.dutra.confluence2wordpress.core.messages.ActionMessagesManager;
import fr.dutra.confluence2wordpress.core.permissions.PluginPermissionsManager;
import fr.dutra.confluence2wordpress.core.settings.PluginSettingsManager;
import fr.dutra.confluence2wordpress.wp.WordpressClient;
import fr.dutra.confluence2wordpress.wp.WordpressXmlRpcException;

/**
 * @author Alexandre Dutra
 */
public class SettingsAction extends ConfluenceActionSupport {

	private static final long serialVersionUID = 5175072542211533080L;

    private static final String ERRORS_REQUIRED_KEY = "settings.errors.required.field";

    private static final String ERRORS_INTEGER_KEY = "settings.errors.integer.field";

    private static final String ERRORS_URL_KEY = "settings.errors.url";

    private static final String ERRORS_PING = "settings.errors.ping";

	private static final String ERRORS_TAG_NAME_EMPTY_KEY = "settings.errors.tagName.empty";

	private static final String ERRORS_TAG_NAME_INVALID_KEY = "settings.errors.tagName.invalid";

	private static final String ERRORS_TAG_ATTRIBUTE_EMPTY_KEY = "settings.errors.tagAttribute.empty";

    private static final String MSG_PING = "settings.msg.ping";

    private static final String MSG_UPDATE = "settings.msg.update";

    private static final Pattern TAG_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9]+");

    private String pageUrl;

    private String wordpressXmlRpcRelativePath;

    private String wordpressUserName;

    private String wordpressPassword;

    private String wordpressBlogId;

    private String ignoredConfluenceMacros;

    @SuppressWarnings("unchecked")
	private List<String> tagNames = new XWorkList(String.class);
    
    @SuppressWarnings("unchecked")
    private List<String> tagAttributes = new XWorkList(String.class);
    
    private String wordpressRootUrl;

    private String editPostRelativePath;

    private String syntaxHighlighterPlugin;

    private String proxyHost;

    private String proxyPort;

    private String maxConnections;

    private String allowedConfluenceGroups;
    
    private String allowedConfluenceSpaceKeys;
    
    private PluginPermissionsManager pluginPermissionsManager;

    private PluginSettingsManager pluginSettingsManager;
    
    private MacroManager macroManager;
    
    private GroupManager groupManager;
    
    private SpaceManager spaceManager;
    
    private ActionMessagesManager actionMessagesManager = new ActionMessagesManager();
    
    public void setPluginSettingsManager(PluginSettingsManager pluginSettingsManager) {
        this.pluginSettingsManager = pluginSettingsManager;
    }
    
    public void setPluginPermissionManager(PluginPermissionsManager pluginPermissionsManager) {
        this.pluginPermissionsManager = pluginPermissionsManager;
    }

    public void setMacroManager(MacroManager macroManager) {
        this.macroManager = macroManager;
    }
    
    public void setGroupManager(GroupManager groupManager) {
        this.groupManager = groupManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public Set<String> getAvailableMacros(){
        Map<String, Macro> macros = macroManager.getMacros();
        return new TreeSet<String>(macros.keySet());
    }

    public Set<String> getAvailableGroups(){
        Set<String> groupNames = new TreeSet<String>();
        try {
            Pager<Group> groups = groupManager.getGroups();
            for (Group group : groups) {
                groupNames.add(group.getName());
            }
        } catch (EntityException e) {
        }
        return groupNames;
    }

    public Set<String> getAvailableSpaceKeys(){
        Set<String> spaceKeys = new TreeSet<String>();
        List<Space> spaces = spaceManager.getAllSpaces();
        for (Space space: spaces) {
            spaceKeys.add(space.getKey());
        }
        return spaceKeys;
    }
    
    @Override
    public boolean isPermitted() {
        return super.isPermitted() && pluginPermissionsManager.checkConfigurationPermission(getRemoteUser());
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank(getWordpressRootUrl())) {
            addActionError(getText(ERRORS_REQUIRED_KEY), new Object[]{getText("settings.form.wordpressRootUrl.label")});
        }
        if (StringUtils.isBlank(getWordpressXmlRpcRelativePath())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, new Object[]{getText("settings.form.wordpressXmlRpcRelativePath.label")}));
        }
        if (StringUtils.isBlank(getEditPostRelativePath())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, new Object[]{getText("settings.form.editPostRelativePath.label")}));
        }
        if (StringUtils.isBlank(getWordpressUserName())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, new Object[]{getText("settings.form.wordpressUserName.label")}));
        }
        if (StringUtils.isBlank(getWordpressPassword())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, new Object[]{getText("settings.form.wordpressPassword.label")}));
        }
        if (StringUtils.isBlank(getWordpressBlogId())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, new Object[]{getText("settings.form.wordpressBlogId.label")}));
        }
        if (StringUtils.isBlank(getSyntaxHighlighterPlugin())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, new Object[]{getText("settings.form.syntaxHighlighterPlugin.label")}));
        }
        if (StringUtils.isBlank(getWordpressMaxConnections())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, new Object[]{getText("settings.form.wordpressMaxConnections.label")}));
        }
        if (StringUtils.isNotBlank(getProxyPort())) {
        	try {
				Integer.decode(getProxyPort());
			} catch (NumberFormatException e) {
				addActionError(getText(ERRORS_INTEGER_KEY, new Object[]{getText("settings.form.proxyPort.label")}));
			}
        }
        if (StringUtils.isNotBlank(getWordpressMaxConnections())) {
        	try {
				Integer.decode(getWordpressMaxConnections());
			} catch (NumberFormatException e) {
				addActionError(getText(ERRORS_INTEGER_KEY, new Object[]{getText("settings.form.wordpressMaxConnections.label")}));
			}
        }
		String url = getWordpressRootUrl() + getWordpressXmlRpcRelativePath();
        try {
			new URL(url);
		} catch (MalformedURLException e) {
			addActionError(getText(ERRORS_URL_KEY, new Object[]{url}));
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
    }
    
    public String input() throws Exception {
        actionMessagesManager.restoreActionErrorsAndMessagesFromSession(this);
        wordpressRootUrl = pluginSettingsManager.getWordpressRootUrl();
        ignoredConfluenceMacros = pluginSettingsManager.getDefaultIgnoredConfluenceMacros();
        wordpressXmlRpcRelativePath = pluginSettingsManager.getWordpressXmlRpcRelativePath();
        wordpressUserName = pluginSettingsManager.getWordpressUserName();
        wordpressPassword = pluginSettingsManager.getWordpressPassword();
        wordpressBlogId = pluginSettingsManager.getWordpressBlogId();
        editPostRelativePath = pluginSettingsManager.getWordpressEditPostRelativePath();
        proxyHost = pluginSettingsManager.getProxyHost();
        proxyPort = pluginSettingsManager.getProxyPort();
        maxConnections = pluginSettingsManager.getWordpressMaxConnections();
        syntaxHighlighterPlugin = pluginSettingsManager.getWordpressSyntaxHighlighterPlugin();
        allowedConfluenceGroups = pluginSettingsManager.getAllowedConfluenceGroups();
        allowedConfluenceSpaceKeys = pluginSettingsManager.getAllowedConfluenceSpaceKeys();
        Map<String, String> tagAttributesMap = pluginSettingsManager.getDefaultTagAttributes();
        for (Entry<String, String> entry : tagAttributesMap.entrySet()) {
        	tagNames.add(entry.getKey());
        	tagAttributes.add(entry.getValue());
		}
        return SUCCESS;
    }

    @Override
    public String execute() throws Exception {
        saveSettings();
        this.addActionMessage(getText(MSG_UPDATE));
        actionMessagesManager.storeActionErrorsAndMessagesInSession(this);
        return SUCCESS;
    }

    public String testConnection() throws Exception{
        saveSettings();
        WordpressClient client = pluginSettingsManager.getWordpressClient();
        try {
            String expected = Long.toString(System.currentTimeMillis());
            String actual = client.ping(expected);
            if(expected.equals(actual)){
                addActionMessage(getText(MSG_PING));
            } else {
                addActionError(getText(ERRORS_PING), "Expected: " + expected + ", actual: " + actual);
            }
        } catch (WordpressXmlRpcException e) {
            addActionError(getText(ERRORS_PING), e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
        }
        actionMessagesManager.storeActionErrorsAndMessagesInSession(this);
        return SUCCESS;
    }

    private void saveSettings() {
        normalizeUrls();
        pluginSettingsManager.setWordpressRootUrl(wordpressRootUrl);
        pluginSettingsManager.setDefaultIgnoredConfluenceMacros(ignoredConfluenceMacros);
        pluginSettingsManager.setWordpressXmlRpcRelativePath(wordpressXmlRpcRelativePath);
        pluginSettingsManager.setWordpressUserName(wordpressUserName);
        pluginSettingsManager.setWordpressPassword(wordpressPassword);
        pluginSettingsManager.setWordpressBlogId(wordpressBlogId);
        pluginSettingsManager.setWordpressEditPostRelativePath(editPostRelativePath);
        pluginSettingsManager.setProxyHost(proxyHost);
        pluginSettingsManager.setProxyPort(proxyPort);
        pluginSettingsManager.setWordpressSyntaxHighlighterPlugin(syntaxHighlighterPlugin);
        pluginSettingsManager.setAllowedConfluenceGroups(allowedConfluenceGroups);
        pluginSettingsManager.setAllowedConfluenceSpaceKeys(allowedConfluenceSpaceKeys);
        pluginSettingsManager.setWordpressMaxConnections(maxConnections);
        Map<String, String> tagAttributesMap = new LinkedHashMap<String, String>();
        for (int i = 0; i < tagNames.size(); i++) {
            tagAttributesMap.put(tagNames.get(i), tagAttributes.get(i));
        }
        pluginSettingsManager.setTagAttributes(tagAttributesMap);
    }

    private void normalizeUrls() {
        if( ! wordpressRootUrl.endsWith("/")){
            wordpressRootUrl += "/";
        }
        if(wordpressXmlRpcRelativePath.startsWith("/")){
            wordpressXmlRpcRelativePath = wordpressXmlRpcRelativePath.substring(1);
        }
        if(editPostRelativePath.startsWith("/")){
            editPostRelativePath = editPostRelativePath.substring(1);
        }
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getWordpressRootUrl() {
        return wordpressRootUrl;
    }

    public void setWordpressRootUrl(String wordpressRootUrl) {
        this.wordpressRootUrl = wordpressRootUrl;
    }

    public String getEditPostRelativePath() {
        return editPostRelativePath;
    }

    public void setEditPostRelativePath(String editPostRelativePath) {
        this.editPostRelativePath = editPostRelativePath;
    }

    public String getWordpressXmlRpcRelativePath() {
        return wordpressXmlRpcRelativePath;
    }

    public void setWordpressXmlRpcRelativePath(String wordpressXmlRpcRelativePath) {
        this.wordpressXmlRpcRelativePath = wordpressXmlRpcRelativePath;
    }

    public String getWordpressUserName() {
        return wordpressUserName;
    }

    public void setWordpressUserName(String wordpressUserName) {
        this.wordpressUserName = wordpressUserName;
    }

    public String getWordpressPassword() {
        return wordpressPassword;
    }

    public void setWordpressPassword(String wordpressPassword) {
        this.wordpressPassword = wordpressPassword;
    }

    public String getWordpressBlogId() {
        return wordpressBlogId;
    }

    public void setWordpressBlogId(String wordpressBlogId) {
        this.wordpressBlogId = wordpressBlogId;
    }

    public String getIgnoredConfluenceMacros() {
        return ignoredConfluenceMacros;
    }

    public void setIgnoredConfluenceMacros(String ignoredConfluenceMacros) {
        this.ignoredConfluenceMacros = ignoredConfluenceMacros;
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

	public String getSyntaxHighlighterPlugin() {
		return syntaxHighlighterPlugin;
	}

	public void setSyntaxHighlighterPlugin(String syntaxHighlighterPlugin) {
		this.syntaxHighlighterPlugin = syntaxHighlighterPlugin;
	}
	
	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public String getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}
	
	public String getWordpressMaxConnections() {
        return maxConnections;
    }

    public void setWordpressMaxConnections(String maxConnections) {
        this.maxConnections = maxConnections;
    }
    
	public SyntaxHighlighterPlugin[] getSyntaxHighlighterPlugins() {
		return SyntaxHighlighterPlugin.values();
	}
    
    public String getAllowedConfluenceGroups() {
        return allowedConfluenceGroups;
    }
    
    public void setAllowedConfluenceGroups(String allowedConfluenceGroups) {
        this.allowedConfluenceGroups = allowedConfluenceGroups;
    }

    public String getAllowedConfluenceSpaceKeys() {
        return allowedConfluenceSpaceKeys;
    }

    public void setAllowedConfluenceSpaceKeys(String allowedConfluenceSpaceKeys) {
        this.allowedConfluenceSpaceKeys = allowedConfluenceSpaceKeys;
    }

}