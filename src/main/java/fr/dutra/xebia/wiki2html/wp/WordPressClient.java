package fr.dutra.xebia.wiki2html.wp;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import fr.dutra.xebia.wiki2html.wp.transport.DefaultProxyAwareXmlRpcTransportFactory;

/**
 * @see "http://codex.wordpress.org/XML-RPC_wp"
 * 
 * @author Alexandre Dutra
 *
 */
public class WordPressClient {

    private static final String POST_METHOD_NAME = "metaWeblog.newPost";

    private static final String GET_PAGE_METHOD_NAME = "blogger.getPost";

    /**
     * Very, VERY old version of the lib bundled with Confluence.
     * Does not even know about Lists and Maps,
     * only Vectors and Hashtables.
     */
    private XmlRpcClient client;

    private WordPressConnection wordPressConnection;

    public WordPressClient(WordPressConnection wordPressConnection) {
        this.client = new XmlRpcClient(wordPressConnection.getUrl());
        this.wordPressConnection = wordPressConnection;
    }


    public WordPressClient(WordPressConnection wordPressConnection, String proxyHost, int proxyPort) {
        this.client = new XmlRpcClient(
            wordPressConnection.getUrl(),
            new DefaultProxyAwareXmlRpcTransportFactory(
                wordPressConnection.getUrl(), proxyHost, proxyPort));
        this.wordPressConnection = wordPressConnection;
    }

    /**
     * http://xmlrpc.free-conversant.com/docs/bloggerAPI#getPost
     * http://joysofprogramming.com/blogger-getpost/
     * http://stackoverflow.com/questions/3083039/how-can-i-get-a-post-with-xml-rpc-in-wordpress
     * 
     * @param postId
     * @return post
     * @throws XmlRpcException
     * @throws IOException
     */
    public WordPressPost findPostById(int postId) throws XmlRpcException, IOException {
        Vector<Object> params = new Vector<Object>();
        params.add(wordPressConnection.getBlogId());
        params.add(postId);
        params.add(wordPressConnection.getUsername());
        params.add(wordPressConnection.getPassword());
        Map<String, Object> map;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapTemp = (Map<String, Object>) client.execute(GET_PAGE_METHOD_NAME, params);
            map = mapTemp;
        } catch(XmlRpcException e) {
            if(e.getCause() != null && "Unparseable date: \"T::\"".equals(e.getCause().getMessage())){
                //Means WordPressConnection didn't find the post and sent an empty XML back.
                return null;
            }
            throw e;
        }

        /*Sample of the response structure:
            userid=1
            content=<title>Hello world!</title><category>1</category>Welcome
            dateCreated=Tue May 03 15:15:43 CEST 2011
            postid=1
         */

        WordPressPost post = new WordPressPost();
        post.setPostId(postId);
        post.setDraft(false);

        Object authorId = map.get("userid");
        post.setAuthorId(authorId == null ? null : Integer.valueOf(authorId.toString()));

        Date dateCreated = (Date) map.get("dateCreated");
        post.setDateCreated(dateCreated);

        String body = (String) map.get("content");
        String title = null;
        if(body.startsWith("<title>")) {
            title = StringUtils.substringBetween(body, "<title>", "</title>");
            body = StringUtils.substringAfter(body, "</title>");
        }
        int[] categoryIds = null;
        if(body.startsWith("<category>")) {
            String categories = StringUtils.substringBetween(body, "<category>", "</category>");
            String[] tokens = categories.split(",");
            categoryIds = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                categoryIds[i] = Integer.parseInt(tokens[i]);
            }
            body = StringUtils.substringAfter(body, "</category>");
        }
        post.setBody(body);
        post.setTitle(title);
        post.setCategoryIds(categoryIds);

        return post;
    }

    /**
     * L'utilisateur doit être:
     * Editor ou Administrator
     * mais PAS:
     * Author, Contributor, Subscriber
     * 
     * http://mindsharestrategy.com/wp-xmlrpc-metaweblog/
     * http://www.perkiset.org/forum/perl/metaweblognewpost_to_wordpress_blog_xmlrpcphp-t1307.0.html
     * http://joysofprogramming.com/wordpress-xmlrpc-metaweblog-newpost/
     * http://life.mysiteonline.org/archives/161-Automatic-Post-Creation-with-Wordpress,-PHP,-and-XML-RPC.html
     * 
     * @param post to create
     * @return the created post
     * @throws XmlRpcException
     * @throws IOException
     */
    public WordPressPost post(WordPressPost post) throws XmlRpcException, IOException {

        Vector<Object> params = new Vector<Object>();

        params.add(wordPressConnection.getBlogId());
        params.add(wordPressConnection.getUsername());
        params.add(wordPressConnection.getPassword());

        Hashtable<String,Object> map = new Hashtable<String,Object>();
        if(post.getTitle() != null) {
            map.put("title", post.getTitle());
        }
        if(post.getCategoryNames() != null) {
            map.put("categories", post.getCategoryNames());
        }
        if(post.getBody() != null) {
            map.put("description", post.getBody());
        }
        if(post.getTagNames() != null) {
            map.put("mt_keywords", post.getTagNames());
        }
        if(post.getAuthorId() != null) {
            map.put("wp_author_id", post.getAuthorId());
        }
        if(post.getPostSlug() != null) {
            map.put("wp_slug", post.getPostSlug());
        }
        params.add(map);

        params.add(post.isDraft());

        Object ret = client.execute(POST_METHOD_NAME, params);

        int postId = Integer.parseInt(ret.toString());
        post.setPostId(postId);

        return post;
    }

    public static void main(String[] args) throws Exception {
        // the url of your xmlrpc.php, typically
        // of the form http://your.domain.here/wordpress/xmlrpc.php
        String xmlRpcUrl = "http://blog.xebia.fr/xmlrpc.php";
        xmlRpcUrl = "http://localhost/wordpress/xmlrpc.php";

        URL url = new URL(xmlRpcUrl);
        String userName = "adutra";
        String password = "80Onizofr";
        String blogId = "1";

        WordPressConnection wordPressConnection = new WordPressConnection(url, userName, password, blogId);

        WordPressClient client = new WordPressClient(
            wordPressConnection
            //,"proxy.gicm.net", 3128
        );

        WordPressPost post = client.findPostById(40);

        System.out.println(post);

        post = new WordPressPost();
        post.setAuthorId(3);
        post.setTitle("Revue de Presse Xebia");
        post.setBody("coucou c'est un draft");
        post.setCategoryNames("test", "newcat");//categories must exist.
        post.setTagNames("tag1", "tag2", "newtag"); //tags are dynamically created.
        post = client.post(post);
        System.out.println(post);

    }
}