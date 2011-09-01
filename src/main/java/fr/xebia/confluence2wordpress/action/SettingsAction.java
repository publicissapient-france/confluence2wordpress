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
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;

import fr.xebia.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;
import fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager;

/**
 * @author Alexandre Dutra
 */
public class SettingsAction extends ConfluenceActionSupport {

    private static final long serialVersionUID = 5175072542211533080L;

    private static final String ERRORS_REQUIRED_KEY = "settings.errors.required.field";

    private UserManager userManager;

    private String pageUrl;

    private String wordpressXmlRpcUrl;

    private String wordpressUserName;

    private String wordpressPassword;

    private String wordpressBlogId;

    private String ignoredConfluenceMacros;

    private String wordpressRootUrl;

    private String editPostUrl;

    private String syntaxHighlighterPlugin;

    private PluginSettingsManager pluginSettingsManager;

    public void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsManager = new PluginSettingsManager();
        this.pluginSettingsManager.setPluginSettingsFactory(pluginSettingsFactory);
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
    }
    
    public String input() throws Exception {

        User remoteUser = getRemoteUser();

        if (!userManager.isAdmin(remoteUser.getName())) {
            return LOGIN;
        }

        wordpressRootUrl = pluginSettingsManager.getWordpressRootUrl();
        ignoredConfluenceMacros = pluginSettingsManager.getDefaultIgnoredConfluenceMacros();
        wordpressXmlRpcUrl = pluginSettingsManager.getWordpressXmlRpcUrl();
        wordpressUserName = pluginSettingsManager.getWordpressUserName();
        wordpressPassword = pluginSettingsManager.getWordpressPassword();
        wordpressBlogId = pluginSettingsManager.getWordpressBlogId();
        editPostUrl = pluginSettingsManager.getWordpressEditPostUrl();
        syntaxHighlighterPlugin = pluginSettingsManager.getWordpressSyntaxHighlighterPlugin();

        return SUCCESS;
    }

    @Override
    public String execute() throws Exception {

        User remoteUser = getRemoteUser();

        if (!userManager.isAdmin(remoteUser.getName())) {
            return LOGIN;
        }

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
        pluginSettingsManager.setWordpressSyntaxHighlighterPlugin(syntaxHighlighterPlugin);

        return SUCCESS;
    }


    /**
     * Beware that the property "userManager" would be mapped to com.atlassian.user.UserManager
     * instead of com.atlassian.sal.api.user.UserManager.
     * @param userManager
     */
    public void setSalUserManager(UserManager userManager) {
        this.userManager = userManager;
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

	public SyntaxHighlighterPlugin[] getSyntaxHighlighterPlugins() {
		return SyntaxHighlighterPlugin.values();
	}

}