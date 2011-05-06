package fr.dutra.xebia.confluence2wordpress.xmlrpc;

import com.atlassian.confluence.rpc.SecureRpc;

public interface WordPressConverter extends SecureRpc {

    String renderContent(String token, String pageUrl, String uploadedFilesBaseUrl) throws Exception;

}