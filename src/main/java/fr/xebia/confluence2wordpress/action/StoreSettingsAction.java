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

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;

/**
 * @author Alexandre Dutra
 */
public class StoreSettingsAction extends ConfluenceActionSupport {

    private static final long serialVersionUID = 1L;

    private final UserManager userManager;

    private final PluginSettingsFactory pluginSettingsFactory;

    private String wordpressXmlRpcUrl = "http://localhost/wordpress/xmlrpc.php";

    private String wordpressUserName = "admin";

    private String wordpressPassword = "admin";

    private String wordpressBlogId = "1";

    public StoreSettingsAction(UserManager userManager, PluginSettingsFactory pluginSettingsFactory) {
        super();
        this.userManager = userManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public String execute() throws Exception {
        User remoteUser = getRemoteUser();
        if (!userManager.isSystemAdmin(remoteUser.getName())) {
            return LOGIN;
        }
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put("fr.xebia.confluence2wordpress:wordpressXmlRpcUrl", wordpressXmlRpcUrl);
        pluginSettings.put("fr.xebia.confluence2wordpress:wordpressUserName", wordpressUserName);
        pluginSettings.put("fr.xebia.confluence2wordpress:wordpressPassword", wordpressPassword);
        pluginSettings.put("fr.xebia.confluence2wordpress:wordpressBlogId", wordpressBlogId);
        return SUCCESS;
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



}