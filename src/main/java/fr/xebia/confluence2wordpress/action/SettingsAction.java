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

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;

/**
 * @author Alexandre Dutra
 */
public class SettingsAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private UserManager userManager;

    private String pageUrl;

    private String wordpressXmlRpcUrl;

    private String wordpressUserName;

    private String wordpressPassword;

    private String wordpressBlogId;

    private String ignoreConfluenceMacros;

    private String resourcesBaseUrl;

    private String wordpressRootUrl;

    private String editPostUrl;

    public String input() throws Exception {

        User remoteUser = getRemoteUser();

        if (!userManager.isAdmin(remoteUser.getName())) {
            return LOGIN;
        }

        wordpressRootUrl = getDefaultWordpressRootUrl();
        resourcesBaseUrl = getDefaultResourcesBaseUrl();
        ignoreConfluenceMacros = getDefaultIgnoreConfluenceMacros();
        wordpressXmlRpcUrl = getDefaultWordpressXmlRpcUrl();
        wordpressUserName = getDefaultWordpressUserName();
        wordpressPassword = getDefaultWordpressPassword();
        wordpressBlogId = getDefaultWordpressBlogId();
        editPostUrl = getDefaultWordpressEditPostUrl();

        return SUCCESS;
    }

    @Override
    public String execute() throws Exception {

        User remoteUser = getRemoteUser();

        if (!userManager.isAdmin(remoteUser.getName())) {
            return LOGIN;
        }

        setDefaultWordpressRootUrl(wordpressRootUrl);
        setDefaultResourcesBaseUrl(resourcesBaseUrl);
        setDefaultIgnoreConfluenceMacros(ignoreConfluenceMacros);
        setDefaultWordpressXmlRpcUrl(wordpressXmlRpcUrl);
        setDefaultWordpressUserName(wordpressUserName);
        setDefaultWordpressPassword(wordpressPassword);
        setDefaultWordpressBlogId(wordpressBlogId);
        setDefaultWordpressEditPostUrl(editPostUrl);

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

    public String getIgnoreConfluenceMacros() {
        return ignoreConfluenceMacros;
    }

    public void setIgnoreConfluenceMacros(String ignoreConfluenceMacros) {
        this.ignoreConfluenceMacros = ignoreConfluenceMacros;
    }

    public String getResourcesBaseUrl() {
        return resourcesBaseUrl;
    }

    public void setResourcesBaseUrl(String resourcesBaseUrl) {
        this.resourcesBaseUrl = resourcesBaseUrl;
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


}