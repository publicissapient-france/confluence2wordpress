package fr.xebia.confluence2wordpress.wp;


public interface WordpressConnectionProperties {

    String getWordpressXmlRpcUrl();

    String getWordpressUserName();

    String getWordpressPassword();

    String getWordpressBlogId();

    String getProxyHost();

    String getProxyPort();

}
