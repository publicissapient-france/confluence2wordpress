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

import java.util.List;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import fr.xebia.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;
import fr.xebia.confluence2wordpress.util.CollectionUtils;

/**
 * @author Alexandre Dutra
 *
 */
public class DefaultPluginSettingsManager implements PluginSettingsManager {

    private static final String KEY_PREFIX = "fr.xebia.confluence2wordpress:";

    private static final String DEFAULT_WP_XML_RPC_USERNAME = "admin";

    private static final String DEFAULT_WP_XML_RPC_PASSWORD = "admin";

    private static final String DEFAULT_WP_XML_RPC_BLOG_ID = "1";

    private static final String DEFAULT_IGNORED_CONFLUENCE_MACROS = "tip, info, note, warning";

    private static final String DEFAULT_WORDPRESS_ROOT_URL = "http://localhost/wordpress/";

    private static final String DEFAULT_WP_XML_RPC_RELATIVE_PATH = "xmlrpc.php";

    private static final String DEFAULT_WP_EDIT_POST_RELATIVE_PATH = "wp-admin/post.php?action=edit&post={0}";

    private static final String DEFAULT_WP_SH_PLUGIN = SyntaxHighlighterPlugin.SH_EVOLVED.name();

    private PluginSettingsFactory pluginSettingsFactory;

    public DefaultPluginSettingsManager(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    private String retrieveSettings(String settingsKey, String defaultValue) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        String value = (String) pluginSettings.get(KEY_PREFIX + settingsKey);
        if(value == null) {
            storeSettings(settingsKey, defaultValue);
            value = defaultValue;
        }
        return value;
    }

    private void storeSettings(String settingsKey, String value) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put(KEY_PREFIX + settingsKey, value);
    }


    @Override
    public String getWordpressXmlRpcUrl() {
        return getWordpressRootUrl() + getWordpressXmlRpcRelativePath();
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setWordpressXmlRpcRelativePath(java.lang.String)
     */
    @Override
    public void setWordpressXmlRpcRelativePath(String wordpressXmlRpcRelativePath){
        storeSettings("wordpressXmlRpcRelativePath", wordpressXmlRpcRelativePath);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getWordpressXmlRpcRelativePath()
     */
    @Override
    public String getWordpressXmlRpcRelativePath(){
        return retrieveSettings("wordpressXmlRpcRelativePath", DEFAULT_WP_XML_RPC_RELATIVE_PATH);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setProxyHost(java.lang.String)
     */
    @Override
    public void setProxyHost(String proxyHost){
        storeSettings("proxyHost", proxyHost);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getProxyHost()
     */
    @Override
    public String getProxyHost(){
        return retrieveSettings("proxyHost", null);
    }
    
    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setProxyPort(java.lang.String)
     */
    @Override
    public void setProxyPort(String proxyPort){
        storeSettings("proxyPort", proxyPort);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getProxyPort()
     */
    @Override
    public String getProxyPort(){
        return retrieveSettings("proxyPort", null);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setWordpressEditPostRelativePath(java.lang.String)
     */
    @Override
    public void setWordpressEditPostRelativePath(String wordpressXmlRpcRelativePath){
        storeSettings("wordpressEditPostRelativePath", wordpressXmlRpcRelativePath);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getWordpressEditPostRelativePath()
     */
    @Override
    public String getWordpressEditPostRelativePath(){
        return retrieveSettings("wordpressEditPostRelativePath", DEFAULT_WP_EDIT_POST_RELATIVE_PATH);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setWordpressUserName(java.lang.String)
     */
    @Override
    public void setWordpressUserName(String wordpressUserName){
        storeSettings("wordpressUserName", wordpressUserName);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getWordpressUserName()
     */
    @Override
    public String getWordpressUserName(){
        return retrieveSettings("wordpressUserName", DEFAULT_WP_XML_RPC_USERNAME);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setWordpressPassword(java.lang.String)
     */
    @Override
    public void setWordpressPassword(String wordpressPassword){
        storeSettings("wordpressPassword", wordpressPassword);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getWordpressPassword()
     */
    @Override
    public String getWordpressPassword(){
        return retrieveSettings("wordpressPassword", DEFAULT_WP_XML_RPC_PASSWORD);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setWordpressBlogId(java.lang.String)
     */
    @Override
    public void setWordpressBlogId(String wordpressBlogId){
        storeSettings("wordpressBlogId", wordpressBlogId);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getWordpressBlogId()
     */
    @Override
    public String getWordpressBlogId(){
        return retrieveSettings("wordpressBlogId", DEFAULT_WP_XML_RPC_BLOG_ID);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setDefaultIgnoredConfluenceMacros(java.lang.String)
     */
    @Override
    public void setDefaultIgnoredConfluenceMacros(String ignoredConfluenceMacros){
        storeSettings("ignoredConfluenceMacros", ignoredConfluenceMacros);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getDefaultIgnoredConfluenceMacros()
     */
    @Override
    public String getDefaultIgnoredConfluenceMacros(){
        return retrieveSettings("ignoredConfluenceMacros", DEFAULT_IGNORED_CONFLUENCE_MACROS);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getDefaultIgnoredConfluenceMacrosAsList()
     */
    @Override
    public List<String> getDefaultIgnoredConfluenceMacrosAsList(){
        return CollectionUtils.split(getDefaultIgnoredConfluenceMacros(), ",");
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setAllowedConfluenceGroups(java.lang.String)
     */
    @Override
    public void setAllowedConfluenceGroups(String allowedConfluenceGroups){
        storeSettings("allowedConfluenceGroups", allowedConfluenceGroups);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getAllowedConfluenceGroups()
     */
    @Override
    public String getAllowedConfluenceGroups(){
        return retrieveSettings("allowedConfluenceGroups", null);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getAllowedConfluenceGroupsAsList()
     */
    @Override
    public List<String> getAllowedConfluenceGroupsAsList(){
        return CollectionUtils.split(getAllowedConfluenceGroups(), ",");
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setAllowedConfluenceSpaceKeys(java.lang.String)
     */
    @Override
    public void setAllowedConfluenceSpaceKeys(String allowedConfluenceSpaceKeys) {
        storeSettings("allowedConfluenceSpaceKeys", allowedConfluenceSpaceKeys);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getAllowedConfluenceSpaceKeys()
     */
    @Override
    public String getAllowedConfluenceSpaceKeys() {
        return retrieveSettings("allowedConfluenceSpaceKeys", null);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getAllowedConfluenceSpaceKeysAsList()
     */
    @Override
    public List<String> getAllowedConfluenceSpaceKeysAsList() {
        return CollectionUtils.split(getAllowedConfluenceSpaceKeys(), ",");
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setWordpressRootUrl(java.lang.String)
     */
    @Override
    public void setWordpressRootUrl(String wordpressRootUrl){
        storeSettings("wordpressRootUrl", wordpressRootUrl);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getWordpressRootUrl()
     */
    @Override
    public String getWordpressRootUrl(){
        return retrieveSettings("wordpressRootUrl", DEFAULT_WORDPRESS_ROOT_URL);
    }
    
    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#setWordpressSyntaxHighlighterPlugin(java.lang.String)
     */
    @Override
    public void setWordpressSyntaxHighlighterPlugin(String wordpressSyntaxHighlighterPlugin){
        storeSettings("wordpressSyntaxHighlighterPlugin", wordpressSyntaxHighlighterPlugin);
    }

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getWordpressSyntaxHighlighterPlugin()
     */
    @Override
    public String getWordpressSyntaxHighlighterPlugin(){
        return retrieveSettings("wordpressSyntaxHighlighterPlugin", DEFAULT_WP_SH_PLUGIN);
    }

	/* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager#getWordpressSyntaxHighlighterPluginAsEnum()
     */
	@Override
    public SyntaxHighlighterPlugin getWordpressSyntaxHighlighterPluginAsEnum() {
		return SyntaxHighlighterPlugin.valueOf(getWordpressSyntaxHighlighterPlugin());
	}

}