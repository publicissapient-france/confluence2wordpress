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
import fr.xebia.confluence2wordpress.core.permissions.PluginPermissionsManager;
import fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager;

/**
 * @author Alexandre Dutra
 */
public class SettingsAction extends ConfluenceActionSupport {

    private static final long serialVersionUID = 5175072542211533080L;

    private static final String ERRORS_REQUIRED_KEY = "settings.errors.required.field";

    private static final String ERRORS_INTEGER_KEY = "settings.errors.integer.field";

    private String pageUrl;

    private String wordpressXmlRpcUrl;

    private String wordpressUserName;

    private String wordpressPassword;

    private String wordpressBlogId;

    private String ignoredConfluenceMacros;

    private String wordpressRootUrl;

    private String editPostUrl;

    private String syntaxHighlighterPlugin;

    private String proxyHost;

    private String proxyPort;

    private String allowedConfluenceGroups;
    
    private String allowedConfluenceSpaceKeys;
    
    private PluginPermissionsManager pluginPermissionsManager;

    private PluginSettingsManager pluginSettingsManager;
    
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
            addActionError(getText(ERRORS_REQUIRED_KEY, "settings.form.wordpressRootUrl.label"));
        }
        if (StringUtils.isBlank(getWordpressXmlRpcUrl())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, "settings.form.wordpressXmlRpcUrl.label"));
        }
        if (StringUtils.isBlank(getEditPostUrl())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, "settings.form.editPostUrl.label"));
        }
        if (StringUtils.isBlank(getWordpressUserName())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, "settings.form.wordpressUserName.label"));
        }
        if (StringUtils.isBlank(getWordpressPassword())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, "settings.form.wordpressPassword.label"));
        }
        if (StringUtils.isBlank(getWordpressBlogId())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, "settings.form.wordpressBlogId.label"));
        }
        if (StringUtils.isBlank(getSyntaxHighlighterPlugin())) {
            addActionError(getText(ERRORS_REQUIRED_KEY, "settings.form.syntaxHighlighterPlugin.label"));
        }
        if (StringUtils.isNotBlank(getProxyPort())) {
        	try {
				Integer.decode(getProxyPort());
			} catch (NumberFormatException e) {
				addActionError(getText(ERRORS_INTEGER_KEY, "settings.form.proxyPort.label"));
			}
        }
    }
    
    public String input() throws Exception {
        wordpressRootUrl = pluginSettingsManager.getWordpressRootUrl();
        ignoredConfluenceMacros = pluginSettingsManager.getDefaultIgnoredConfluenceMacros();
        wordpressXmlRpcUrl = pluginSettingsManager.getWordpressXmlRpcUrl();
        wordpressUserName = pluginSettingsManager.getWordpressUserName();
        wordpressPassword = pluginSettingsManager.getWordpressPassword();
        wordpressBlogId = pluginSettingsManager.getWordpressBlogId();
        editPostUrl = pluginSettingsManager.getWordpressEditPostUrl();
        proxyHost = pluginSettingsManager.getProxyHost();
        proxyPort = pluginSettingsManager.getProxyPort();
        syntaxHighlighterPlugin = pluginSettingsManager.getWordpressSyntaxHighlighterPlugin();
        allowedConfluenceGroups = pluginSettingsManager.getAllowedConfluenceGroups();
        allowedConfluenceSpaceKeys = pluginSettingsManager.getAllowedConfluenceSpaceKeys();
        
        return SUCCESS;
    }

    @Override
    public String execute() throws Exception {
        if( ! wordpressRootUrl.endsWith("/")){
            wordpressRootUrl += "/";
        }
        if(wordpressXmlRpcUrl.startsWith("/")){
            wordpressXmlRpcUrl = wordpressXmlRpcUrl.substring(1);
        }
        if(editPostUrl.startsWith("/")){
            editPostUrl = editPostUrl.substring(1);
        }

        pluginSettingsManager.setWordpressRootUrl(wordpressRootUrl);
        pluginSettingsManager.setDefaultIgnoredConfluenceMacros(ignoredConfluenceMacros);
        pluginSettingsManager.setWordpressXmlRpcUrl(wordpressXmlRpcUrl);
        pluginSettingsManager.setWordpressUserName(wordpressUserName);
        pluginSettingsManager.setWordpressPassword(wordpressPassword);
        pluginSettingsManager.setWordpressBlogId(wordpressBlogId);
        pluginSettingsManager.setWordpressEditPostUrl(editPostUrl);
        pluginSettingsManager.setProxyHost(proxyHost);
        pluginSettingsManager.setProxyPort(proxyPort);
        pluginSettingsManager.setWordpressSyntaxHighlighterPlugin(syntaxHighlighterPlugin);
        pluginSettingsManager.setAllowedConfluenceGroups(allowedConfluenceGroups);
        pluginSettingsManager.setAllowedConfluenceSpaceKeys(allowedConfluenceSpaceKeys);
        
        return SUCCESS;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getWordpressXmlRpcUrl() {
        return wordpressXmlRpcUrl;
    }

    public void setWordpressXmlRpcUrl(String wordpressXmlRpcUrl) {
        this.wordpressXmlRpcUrl = wordpressXmlRpcUrl;
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

    public String getWordpressRootUrl() {
        return wordpressRootUrl;
    }

    public void setWordpressRootUrl(String wordpressRootUrl) {
        this.wordpressRootUrl = wordpressRootUrl;
    }

    public String getEditPostUrl() {
        return editPostUrl;
    }

    public void setEditPostUrl(String editPostUrl) {
        this.editPostUrl = editPostUrl;
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