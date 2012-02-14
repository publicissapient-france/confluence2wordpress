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

import java.net.URL;


public class WordpressConnection {

    /**
     * Wordpress XML RPC URL, typically http://your.domain.here/wordpress/xmlrpc.php
     */
    private final URL url;

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

    private final int maxConnections;

    private String proxyHost;
    
    private Integer proxyPort;
    
    public WordpressConnection(URL url, String username, String password, int maxConnections) {
        //default (bogus) blog ID
        this(url, username, password, "1", maxConnections);
    }

    public WordpressConnection(URL url, String username, String password, String blogId, int maxConnections) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.blogId = blogId;
        this.maxConnections = maxConnections;
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

    public URL getUrl() {
        return url;
    }
    
    public int getMaxConnections() {
		return maxConnections;
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