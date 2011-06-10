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

    public WordpressConnection(URL url, String username, String password) {
        //default (bogus) blog ID
        this(url, username, password, "1");
    }

    public WordpressConnection(URL url, String username, String password, String blogId) {
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

    public URL getUrl() {
        return url;
    }


}