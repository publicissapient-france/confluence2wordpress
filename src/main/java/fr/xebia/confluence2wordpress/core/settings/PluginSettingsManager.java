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
package fr.xebia.confluence2wordpress.core.settings;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import fr.xebia.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;
import fr.xebia.confluence2wordpress.wp.WordpressClient;
import fr.xebia.confluence2wordpress.wp.WordpressConnection;

/**
 * @author Alexandre Dutra
 *
 */
public class PluginSettingsManager {

    private static final String DEFAULT_WP_XML_RPC_USERNAME = "admin";

    private static final String DEFAULT_WP_XML_RPC_PASSWORD = "admin";

    private static final String DEFAULT_WP_XML_RPC_BLOG_ID = "1";

    private static final String DEFAULT_IGNORED_CONFLUENCE_MACROS = "tip info note warning";

    private static final String DEFAULT_WORDPRESS_ROOT_URL = "http://localhost/wordpress";

    private static final String DEFAULT_WP_XML_RPC_URL = "/xmlrpc.php";

    private static final String DEFAULT_WP_EDIT_POST_URL = "/wp-admin/post.php?action=edit&post={0}";

    private static final String DEFAULT_WP_SH_PLUGIN = SyntaxHighlighterPlugin.SH_EVOLVED.name();

    private PluginSettingsFactory pluginSettingsFactory;

    public void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public <T> T retrieveSettings(String settingsKey, T defaultValue) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        @SuppressWarnings("unchecked")
        T value = (T) pluginSettings.get("fr.xebia.confluence2wordpress:" + settingsKey);
        if(value == null) {
            storeSettings(settingsKey, defaultValue);
            value = defaultValue;
        }
        return value;
    }

    public void storeSettings(String settingsKey, Object value) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put("fr.xebia.confluence2wordpress:" + settingsKey, value);
    }

    public void setWordpressXmlRpcUrl(String wordpressXmlRpcUrl){
        storeSettings("wordpressXmlRpcUrl", wordpressXmlRpcUrl);
    }

    public String getWordpressXmlRpcUrl(){
        return retrieveSettings("wordpressXmlRpcUrl", DEFAULT_WP_XML_RPC_URL);
    }

    public void setProxyHost(String proxyHost){
        storeSettings("proxyHost", proxyHost);
    }

    public String getProxyHost(){
        return retrieveSettings("proxyHost", null);
    }
    
    public void setProxyPort(String proxyPort){
        storeSettings("proxyPort", proxyPort);
    }

    public String getProxyPort(){
        return retrieveSettings("proxyPort", null);
    }

    public void setWordpressEditPostUrl(String wordpressXmlRpcUrl){
        storeSettings("wordpressEditPostUrl", wordpressXmlRpcUrl);
    }

    public String getWordpressEditPostUrl(){
        return retrieveSettings("wordpressEditPostUrl", DEFAULT_WP_EDIT_POST_URL);
    }

    public void setWordpressUserName(String wordpressUserName){
        storeSettings("wordpressUserName", wordpressUserName);
    }

    public String getWordpressUserName(){
        return retrieveSettings("wordpressUserName", DEFAULT_WP_XML_RPC_USERNAME);
    }

    public void setWordpressPassword(String wordpressPassword){
        storeSettings("wordpressPassword", wordpressPassword);
    }

    public String getWordpressPassword(){
        return retrieveSettings("wordpressPassword", DEFAULT_WP_XML_RPC_PASSWORD);
    }

    public void setWordpressBlogId(String wordpressBlogId){
        storeSettings("wordpressBlogId", wordpressBlogId);
    }

    public String getWordpressBlogId(){
        return retrieveSettings("wordpressBlogId", DEFAULT_WP_XML_RPC_BLOG_ID);
    }

    public void setDefaultIgnoredConfluenceMacros(String ignoredConfluenceMacros){
        storeSettings("ignoredConfluenceMacros", ignoredConfluenceMacros);
    }

    public String getDefaultIgnoredConfluenceMacros(){
        return retrieveSettings("ignoredConfluenceMacros", DEFAULT_IGNORED_CONFLUENCE_MACROS);
    }

    public List<String> getDefaultIgnoredConfluenceMacrosAsList(){
        return Arrays.asList(StringUtils.split(getDefaultIgnoredConfluenceMacros()));
    }

    public void setWordpressRootUrl(String wordpressRootUrl){
        storeSettings("wordpressRootUrl", wordpressRootUrl);
    }

    public String getWordpressRootUrl(){
        return retrieveSettings("wordpressRootUrl", DEFAULT_WORDPRESS_ROOT_URL);
    }
    
    public void setWordpressSyntaxHighlighterPlugin(String wordpressSyntaxHighlighterPlugin){
        storeSettings("wordpressSyntaxHighlighterPlugin", wordpressSyntaxHighlighterPlugin);
    }

    public String getWordpressSyntaxHighlighterPlugin(){
        return retrieveSettings("wordpressSyntaxHighlighterPlugin", DEFAULT_WP_SH_PLUGIN);
    }

	public SyntaxHighlighterPlugin getWordpressSyntaxHighlighterPluginAsEnum() {
		return SyntaxHighlighterPlugin.valueOf(getWordpressSyntaxHighlighterPlugin());
	}

    public WordpressClient newWordpressClient() {
        WordpressConnection wordpressConnection = new WordpressConnection(
            getWordpressRootUrl() + getWordpressXmlRpcUrl(),
            getWordpressUserName(),
            getWordpressPassword(),
            getWordpressBlogId());
        wordpressConnection.setProxyHost(getProxyHost());
        String proxyPort = getProxyPort();
		wordpressConnection.setProxyPort(proxyPort == null ? null : Integer.valueOf(proxyPort));
        return new WordpressClient(wordpressConnection);
    }

}