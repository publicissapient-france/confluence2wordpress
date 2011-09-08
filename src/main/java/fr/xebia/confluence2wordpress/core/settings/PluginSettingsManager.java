package fr.xebia.confluence2wordpress.core.settings;

import java.util.List;

import fr.xebia.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;
import fr.xebia.confluence2wordpress.wp.WordpressClient;

public interface PluginSettingsManager {

    void setWordpressXmlRpcUrl(String wordpressXmlRpcUrl);

    String getWordpressXmlRpcUrl();

    void setProxyHost(String proxyHost);

    String getProxyHost();

    void setProxyPort(String proxyPort);

    String getProxyPort();

    void setWordpressEditPostUrl(String wordpressXmlRpcUrl);

    String getWordpressEditPostUrl();

    void setWordpressUserName(String wordpressUserName);

    String getWordpressUserName();

    void setWordpressPassword(String wordpressPassword);

    String getWordpressPassword();

    void setWordpressBlogId(String wordpressBlogId);

    String getWordpressBlogId();

    void setDefaultIgnoredConfluenceMacros(String ignoredConfluenceMacros);

    String getDefaultIgnoredConfluenceMacros();

    List<String> getDefaultIgnoredConfluenceMacrosAsList();

    void setAllowedConfluenceGroups(String allowedConfluenceGroups);

    String getAllowedConfluenceGroups();

    List<String> getAllowedConfluenceGroupsAsList();

    void setAllowedConfluenceSpaceKeys(String allowedConfluenceSpaceKeys);

    String getAllowedConfluenceSpaceKeys();

    List<String> getAllowedConfluenceSpaceKeysAsList();

    void setWordpressRootUrl(String wordpressRootUrl);

    String getWordpressRootUrl();

    void setWordpressSyntaxHighlighterPlugin(String wordpressSyntaxHighlighterPlugin);

    String getWordpressSyntaxHighlighterPlugin();

    SyntaxHighlighterPlugin getWordpressSyntaxHighlighterPluginAsEnum();

    WordpressClient newWordpressClient();

}