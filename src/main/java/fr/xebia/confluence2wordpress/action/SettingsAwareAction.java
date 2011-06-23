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

import java.util.Date;
import java.util.IllegalFormatException;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * @author Alexandre Dutra
 *
 */
public abstract class SettingsAwareAction extends ConfluenceActionSupport {

    private static final String DEFAULT_WP_XML_RPC_USERNAME = "admin";

    private static final String DEFAULT_WP_XML_RPC_PASSWORD = "admin";

    private static final String DEFAULT_WP_XML_RPC_BLOG_ID = "1";

    private static final String DEFAULT_IGNORE_CONFLUENCE_MACROS = "info warning";

    private static final String DEFAULT_WORDPRESS_ROOT_URL = "http://localhost/wordpress";

    private static final String DEFAULT_RESOURCES_BASE_URL = DEFAULT_WORDPRESS_ROOT_URL + "/wp-content/uploads/%1$tY/%1$tm/";

    private static final String DEFAULT_WP_XML_RPC_URL = DEFAULT_WORDPRESS_ROOT_URL + "/xmlrpc.php";

    private static final String DEFAULT_WP_EDIT_POST_URL = DEFAULT_WORDPRESS_ROOT_URL + "/wp-admin/post.php?action=edit&post={0}";

    private static final long serialVersionUID = 1L;

    private PluginSettingsFactory pluginSettingsFactory;

    public void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    protected <T> T retrieveSettings(String settingsKey, T defaultValue) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        @SuppressWarnings("unchecked")
        T value = (T) pluginSettings.get("fr.xebia.confluence2wordpress:" + settingsKey);
        if(value == null) {
            storeSettings(settingsKey, defaultValue);
            value = defaultValue;
        }
        return value;
    }

    protected void storeSettings(String settingsKey, Object value) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put("fr.xebia.confluence2wordpress:" + settingsKey, value);
    }

    protected void setDefaultWordpressXmlRpcUrl(String wordpressXmlRpcUrl){
        storeSettings("wordpressXmlRpcUrl", wordpressXmlRpcUrl);
    }

    protected String getDefaultWordpressXmlRpcUrl(){
        return retrieveSettings("wordpressXmlRpcUrl", DEFAULT_WP_XML_RPC_URL);
    }

    protected void setDefaultWordpressEditPostUrl(String wordpressXmlRpcUrl){
        storeSettings("wordpressEditPostUrl", wordpressXmlRpcUrl);
    }

    protected String getDefaultWordpressEditPostUrl(){
        return retrieveSettings("wordpressEditPostUrl", DEFAULT_WP_EDIT_POST_URL);
    }

    protected void setDefaultWordpressUserName(String wordpressUserName){
        storeSettings("wordpressUserName", wordpressUserName);
    }

    protected String getDefaultWordpressUserName(){
        return retrieveSettings("wordpressUserName", DEFAULT_WP_XML_RPC_USERNAME);
    }

    protected void setDefaultWordpressPassword(String wordpressPassword){
        storeSettings("wordpressPassword", wordpressPassword);
    }

    protected String getDefaultWordpressPassword(){
        return retrieveSettings("wordpressPassword", DEFAULT_WP_XML_RPC_PASSWORD);
    }

    protected void setDefaultWordpressBlogId(String wordpressBlogId){
        storeSettings("wordpressBlogId", wordpressBlogId);
    }

    protected String getDefaultWordpressBlogId(){
        return retrieveSettings("wordpressBlogId", DEFAULT_WP_XML_RPC_BLOG_ID);
    }

    protected void setDefaultIgnoreConfluenceMacros(String ignoreConfluenceMacros){
        storeSettings("ignoreConfluenceMacros", ignoreConfluenceMacros);
    }

    protected String getDefaultIgnoreConfluenceMacros(){
        return retrieveSettings("ignoreConfluenceMacros", DEFAULT_IGNORE_CONFLUENCE_MACROS);
    }

    protected void setDefaultWordpressRootUrl(String wordpressRootUrl){
        storeSettings("wordpressRootUrl", wordpressRootUrl);
    }

    protected String getDefaultWordpressRootUrl(){
        return retrieveSettings("wordpressRootUrl", DEFAULT_WORDPRESS_ROOT_URL);
    }

    protected void setDefaultResourcesBaseUrl(String resourcesBaseUrl){
        storeSettings("resourcesBaseUrl", resourcesBaseUrl);
    }

    protected String getDefaultResourcesBaseUrl(){
        return retrieveSettings("resourcesBaseUrl", DEFAULT_RESOURCES_BASE_URL);
    }

    protected String getDefaultResourcesBaseUrlFormatted(){
        String rawUrl = getDefaultResourcesBaseUrl();
        try {
            return String.format(rawUrl, new Date());
        } catch (IllegalFormatException e) {
            return rawUrl;
        }
    }


}