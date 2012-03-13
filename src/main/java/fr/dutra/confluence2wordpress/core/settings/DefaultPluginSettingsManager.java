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
package fr.dutra.confluence2wordpress.core.settings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import fr.dutra.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;
import fr.dutra.confluence2wordpress.util.CollectionUtils;
import fr.dutra.confluence2wordpress.wp.WordpressClient;
import fr.dutra.confluence2wordpress.wp.WordpressConnection;

/**
 * @author Alexandre Dutra
 *
 */
public class DefaultPluginSettingsManager implements PluginSettingsManager {

    private static final String KEY_PREFIX = "fr.dutra.confluence2wordpress:";

    private static final String DEFAULT_WP_XML_RPC_USERNAME = "admin";

    private static final String DEFAULT_WP_XML_RPC_PASSWORD = "admin";

    private static final String DEFAULT_WP_XML_RPC_BLOG_ID = "1";

    private static final String DEFAULT_IGNORED_CONFLUENCE_MACROS = "tip, info, note, warning";

    private static final String DEFAULT_WORDPRESS_ROOT_URL = "http://localhost/wordpress/";

    private static final String DEFAULT_WP_XML_RPC_RELATIVE_PATH = "xmlrpc.php";

    private static final String DEFAULT_WP_EDIT_POST_RELATIVE_PATH = "wp-admin/post.php?action=edit&post={0}";

    private static final String DEFAULT_WP_XML_RPC_MAX_CONNECTIONS = "10";

    private static final String DEFAULT_WP_SH_PLUGIN = SyntaxHighlighterPlugin.SH_EVOLVED.name();

    private static final Map<String,String> DEFAULT_TAG_ATTRIBUTES = new LinkedHashMap<String, String>();

    private PluginSettingsFactory pluginSettingsFactory;

    private WordpressClient client;
    
    public DefaultPluginSettingsManager(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
	public synchronized WordpressClient getWordpressClient() {
		if(client == null) {
			createClient();
		}
		return client;
	}

	@Override
    public String getWordpressXmlRpcUrl() {
        return getWordpressRootUrl() + getWordpressXmlRpcRelativePath();
    }

    @Override
    public void setWordpressXmlRpcRelativePath(String wordpressXmlRpcRelativePath){
    	this.destroyClient();
        storeSettings("wordpressXmlRpcRelativePath", wordpressXmlRpcRelativePath);
    }

    @Override
    public String getWordpressXmlRpcRelativePath(){
        return retrieveSettings("wordpressXmlRpcRelativePath", DEFAULT_WP_XML_RPC_RELATIVE_PATH);
    }

    @Override
    public void setProxyHost(String proxyHost){
    	this.destroyClient();
        storeSettings("proxyHost", proxyHost);
    }

    @Override
    public String getProxyHost(){
        return retrieveSettings("proxyHost", null);
    }
    
    @Override
    public void setProxyPort(String proxyPort){
    	this.destroyClient();
        storeSettings("proxyPort", proxyPort);
    }

    @Override
    public String getProxyPort(){
        return retrieveSettings("proxyPort", null);
    }

	@Override
	public void setWordpressMaxConnections(String maxConnections) {
		this.destroyClient();
        storeSettings("maxConnections", maxConnections);
	}

	@Override
	public String getWordpressMaxConnections() {
		return retrieveSettings("maxConnections", DEFAULT_WP_XML_RPC_MAX_CONNECTIONS);
	}

    @Override
    public void setWordpressUserName(String wordpressUserName){
    	this.destroyClient();
        storeSettings("wordpressUserName", wordpressUserName);
    }

    @Override
    public String getWordpressUserName(){
        return retrieveSettings("wordpressUserName", DEFAULT_WP_XML_RPC_USERNAME);
    }

    @Override
    public void setWordpressPassword(String wordpressPassword){
    	this.destroyClient();
        storeSettings("wordpressPassword", wordpressPassword);
    }

    @Override
    public String getWordpressPassword(){
        return retrieveSettings("wordpressPassword", DEFAULT_WP_XML_RPC_PASSWORD);
    }

    @Override
    public void setWordpressBlogId(String wordpressBlogId){
    	this.destroyClient();
        storeSettings("wordpressBlogId", wordpressBlogId);
    }

    @Override
    public String getWordpressBlogId(){
        return retrieveSettings("wordpressBlogId", DEFAULT_WP_XML_RPC_BLOG_ID);
    }

    @Override
    public void setWordpressEditPostRelativePath(String wordpressXmlRpcRelativePath){
        storeSettings("wordpressEditPostRelativePath", wordpressXmlRpcRelativePath);
    }

    @Override
    public String getWordpressEditPostRelativePath(){
        return retrieveSettings("wordpressEditPostRelativePath", DEFAULT_WP_EDIT_POST_RELATIVE_PATH);
    }

    @Override
    public void setDefaultIgnoredConfluenceMacros(String ignoredConfluenceMacros){
        storeSettings("ignoredConfluenceMacros", ignoredConfluenceMacros);
    }

    @Override
    public String getDefaultIgnoredConfluenceMacros(){
        return retrieveSettings("ignoredConfluenceMacros", DEFAULT_IGNORED_CONFLUENCE_MACROS);
    }

    @Override
    public List<String> getDefaultIgnoredConfluenceMacrosAsList(){
        return CollectionUtils.split(getDefaultIgnoredConfluenceMacros(), ",");
    }

    @Override
    public void setAllowedConfluenceGroups(String allowedConfluenceGroups){
        storeSettings("allowedConfluenceGroups", allowedConfluenceGroups);
    }

    @Override
    public String getAllowedConfluenceGroups(){
        return retrieveSettings("allowedConfluenceGroups", null);
    }

    @Override
    public List<String> getAllowedConfluenceGroupsAsList(){
        return CollectionUtils.split(getAllowedConfluenceGroups(), ",");
    }

    @Override
    public void setAllowedConfluenceSpaceKeys(String allowedConfluenceSpaceKeys) {
        storeSettings("allowedConfluenceSpaceKeys", allowedConfluenceSpaceKeys);
    }

    @Override
    public String getAllowedConfluenceSpaceKeys() {
        return retrieveSettings("allowedConfluenceSpaceKeys", null);
    }

    @Override
    public List<String> getAllowedConfluenceSpaceKeysAsList() {
        return CollectionUtils.split(getAllowedConfluenceSpaceKeys(), ",");
    }

    @Override
    public void setWordpressRootUrl(String wordpressRootUrl){
    	this.destroyClient();
        storeSettings("wordpressRootUrl", wordpressRootUrl);
    }

    @Override
    public String getWordpressRootUrl(){
        return retrieveSettings("wordpressRootUrl", DEFAULT_WORDPRESS_ROOT_URL);
    }
    
    @Override
    public void setWordpressSyntaxHighlighterPlugin(String wordpressSyntaxHighlighterPlugin){
        storeSettings("wordpressSyntaxHighlighterPlugin", wordpressSyntaxHighlighterPlugin);
    }

    @Override
    public String getWordpressSyntaxHighlighterPlugin(){
        return retrieveSettings("wordpressSyntaxHighlighterPlugin", DEFAULT_WP_SH_PLUGIN);
    }

	@Override
    public SyntaxHighlighterPlugin getWordpressSyntaxHighlighterPluginAsEnum() {
		return SyntaxHighlighterPlugin.valueOf(getWordpressSyntaxHighlighterPlugin());
	}

	@Override
	public Map<String, String> getDefaultTagAttributes() {
		return retrieveSettings("tagAttributes", DEFAULT_TAG_ATTRIBUTES);
	}

	@Override
	public void setTagAttributes(Map<String, String> attributes) {
		storeSettings("tagAttributes", attributes);
	}
	
	private <T> T retrieveSettings(String settingsKey, T defaultValue) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        @SuppressWarnings("unchecked")
        T value = (T) pluginSettings.get(KEY_PREFIX + settingsKey);
        if(value == null) {
            storeSettings(settingsKey, defaultValue);
            value = defaultValue;
        }
        return value;
    }

    private void storeSettings(String settingsKey, Object value) {
        PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
        pluginSettings.put(KEY_PREFIX + settingsKey, value);
    }

	private synchronized void createClient() {
		try {
			URL url = new URL(getWordpressXmlRpcUrl());
			WordpressConnection wordpressConnection = new WordpressConnection(
	            url,
	            getWordpressUserName(),
	            getWordpressPassword(),
	            getWordpressBlogId(),
	            Integer.parseInt(getWordpressMaxConnections())
				);
	        wordpressConnection.setProxyHost(getProxyHost());
			wordpressConnection.setProxyPort(StringUtils.isEmpty(getProxyPort()) ? null : Integer.decode(getProxyHost()));
	        this.client = new WordpressClient(wordpressConnection);
		} catch (MalformedURLException e) {
			//this has been validated previously
		}
	}

	private synchronized void destroyClient() {
		if(this.client != null) {
			this.client.destroy();
		}
        this.client = null;
	}

}