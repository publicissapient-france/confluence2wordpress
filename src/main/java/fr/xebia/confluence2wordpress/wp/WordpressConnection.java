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
package fr.xebia.confluence2wordpress.wp;


public class WordpressConnection {

    /**
     * Wordpress XML RPC URL, typically http://your.domain.here/wordpress/xmlrpc.php
     */
    private final String url;

    /**
     * Wordpress user to connect with. The user must have one of
     * the following roles: Editor or Administrator.
     * Other roles like Author, Contributor, Subscriber won't be
     * allowed to connect.
     */
    private final String username;

    private final String password;

    /**
     * The blog ID (for multi-blog installations).
     */
    private final String blogId;
    
    private String proxyHost;
    
    private Integer proxyPort;

    public WordpressConnection(String url, String username, String password) {
        //default (bogus) blog ID
        this(url, username, password, "1");
    }

    public WordpressConnection(String url, String username, String password, String blogId) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.blogId = blogId;
    }

    public String getBlogId() {
        return blogId;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getUrl() {
        return url;
    }
    
    public String getProxyHost() {
        return proxyHost;
    }
    
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    public Integer getProxyPort() {
        return proxyPort;
    }
        
    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }


}