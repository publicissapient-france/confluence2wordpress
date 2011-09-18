/**
 * Copyright 2011 Alexandre Dutra
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.confluence2wordpress.action;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.core.ConfluenceActionSupport;

import fr.xebia.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;
import fr.xebia.confluence2wordpress.core.messages.ActionMessagesManager;
import fr.xebia.confluence2wordpress.core.permissions.PluginPermissionsManager;
import fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager;
import fr.xebia.confluence2wordpress.wp.WordpressClient;
import fr.xebia.confluence2wordpress.wp.WordpressClientFactory;
import fr.xebia.confluence2wordpress.wp.WordpressConnectionProperties;
import fr.xebia.confluence2wordpress.wp.WordpressXmlRpcException;

/**
 * @author Alexandre Dutra
 */
public class SettingsAction extends ConfluenceActionSupport implements WordpressConnectionProperties {

    private static final long serialVersionUID = 5175072542211533080L;

    private static final String ERRORS_REQUIRED_KEY = "settings.errors.required.field";

    private static final String ERRORS_INTEGER_KEY = "settings.errors.integer.field";

    private static final String ERRORS_PING = "settings.errors.ping";

    private static final String MSG_PING = "settings.msg.ping";

    private static final String MSG_UPDATE = "settings.msg.update";

    private String pageUrl;

    private String wordpressXmlRpcRelativePath;

    private String wordpressUserName;

    private String wordpressPassword;

    private String wordpressBlogId;

    private String ignoredConfluenceMacros;

    private String wordpressRootUrl;

    private String editPostRelativePath;

    private String syntaxHighlighterPlugin;

    private String proxyHost;

    private String proxyPort;

    private String allowedConfluenceGroups;
    
    private String allowedConfluenceSpaceKeys;
    
    private PluginPermissionsManager pluginPermissionsManager;

    private PluginSettingsManager pluginSettingsManager;
    
    private WordpressClientFactory wordpressClientFactory = new WordpressClientFactory();
    
    private ActionMessagesManager actionMessagesManager = new ActionMessagesManager();
    
    public void setPluginSettingsManager(PluginSettingsManager pluginSettingsManager) {
        this.pluginSettingsManager = pluginSettingsManager;
    }
    
    public void setPluginPermissionManager(PluginPermissionsManager pluginPermissionsManager) {
        this.pluginPermissionsManager = pluginPermissionsManager;
    }

    @Override
    public boolean isPermitted() {
        return super.isPermitted() && pluginPermissionsManager.checkConfigurationPermission(getRemoteUser());
    }

    @Override
    public void validate() {
        if (StringUtils.isBlank(getWordpressRootUrl())) {
            addActionError(getText(ERRORS_REQUIRED_KEY), getText("settings.form.wordpressRootUrl.label"));
        }
        if (StringUtils.isBlank(getWordpressXmlRpcRelativePath())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, getText("settings.form.wordpressXmlRpcRelativePath.label")));
        }
        if (StringUtils.isBlank(getEditPostRelativePath())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, getText("settings.form.editPostRelativePath.label")));
        }
        if (StringUtils.isBlank(getWordpressUserName())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, getText("settings.form.wordpressUserName.label")));
        }
        if (StringUtils.isBlank(getWordpressPassword())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, getText("settings.form.wordpressPassword.label")));
        }
        if (StringUtils.isBlank(getWordpressBlogId())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, getText("settings.form.wordpressBlogId.label")));
        }
        if (StringUtils.isBlank(getSyntaxHighlighterPlugin())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, getText("settings.form.syntaxHighlighterPlugin.label")));
        }
        if (StringUtils.isNotBlank(getProxyPort())) {
        	try {
				Integer.decode(getProxyPort());
			} catch (NumberFormatException e) {
				addActionError(getText(ERRORS_INTEGER_KEY, getText("settings.form.proxyPort.label")));
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
        syntaxHighlighterPlugin = pluginSettingsManager.getWordpressSyntaxHighlighterPlugin();
        allowedConfluenceGroups = pluginSettingsManager.getAllowedConfluenceGroups();
        allowedConfluenceSpaceKeys = pluginSettingsManager.getAllowedConfluenceSpaceKeys();
        return SUCCESS;
    }

    @Override
    public String execute() throws Exception {
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
        this.addActionMessage(getText(MSG_UPDATE));
        actionMessagesManager.storeActionErrorsAndMessagesInSession(this);
        return SUCCESS;
    }

    public String testConnection(){
        normalizeUrls();
        WordpressClient client = wordpressClientFactory.newWordpressClient(this);
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
        return SUCCESS;
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

    @Override
    public String getWordpressXmlRpcUrl() {
        return getWordpressRootUrl() + getWordpressXmlRpcRelativePath();
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