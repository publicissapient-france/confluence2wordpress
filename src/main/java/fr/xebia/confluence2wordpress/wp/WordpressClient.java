package fr.xebia.confluence2wordpress.wp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import fr.xebia.confluence2wordpress.wp.transport.DefaultProxyAwareXmlRpcTransportFactory;

/**
 * @see "http://codex.wordpress.org/XML-RPC_wp"
 * 
 * @author Alexandre Dutra
 *
 */
public class WordpressClient {

    private static final String POST_METHOD_NAME = "metaWeblog.newPost";

    private static final String GET_PAGE_METHOD_NAME = "blogger.getPost";

    private static final String GET_USERS_METHOD_NAME = "confluence2wordpress.getAuthors"; // "wp.getAuthors";

    private static final String GET_CATEGORIES_METHOD_NAME = "wp.getCategories";

    private static final String GET_TAGS_METHOD_NAME = "wp.getTags";

    /**
     * Very, VERY old version of the lib bundled with Confluence.
     * Does not even know about Lists and Maps,
     * only Vectors and Hashtables.
     */
    private XmlRpcClient client;

    private WordpressConnection wordpressConnection;

    public WordpressClient(WordpressConnection wordpressConnection) {
        this.client = new XmlRpcClient(wordpressConnection.getUrl());
        this.wordpressConnection = wordpressConnection;
    }


    public WordpressClient(WordpressConnection wordpressConnection, String proxyHost, int proxyPort) {
        this.client = new XmlRpcClient(
            wordpressConnection.getUrl(),
            new DefaultProxyAwareXmlRpcTransportFactory(
                wordpressConnection.getUrl(), proxyHost, proxyPort));
        this.wordpressConnection = wordpressConnection;
    }

    /**
     * @see "http://codex.wordpress.org/XML-RPC_wp#wp.getAuthors"
     * @return the list of {@link WordpressUser}s of the blog.
     * @throws XmlRpcException
     * @throws IOException
     */
    public List<WordpressUser> getUsers() throws XmlRpcException, IOException {

        Vector<Object> params = new Vector<Object>();

        params.add(wordpressConnection.getBlogId());
        params.add(wordpressConnection.getUsername());
        params.add(wordpressConnection.getPassword());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>)
        client.execute(GET_USERS_METHOD_NAME, params);

        List<WordpressUser> categories = new ArrayList<WordpressUser>(rows.size());
        for (Map<String, Object> row : rows) {
            WordpressUser author = new WordpressUser();
            author.setId(Integer.valueOf(row.get("user_id").toString()));
            author.setFirstName((String)row.get("first_name"));
            author.setLastName((String)row.get("last_name"));
            author.setNiceName((String) row.get("user_nicename"));
            author.setDisplayName((String)row.get("display_name"));
            author.setLevel(Integer.valueOf(row.get("user_level").toString()));
            categories.add(author);
        }

        return categories;
    }


    /**
     * @see "http://codex.wordpress.org/XML-RPC_wp#wp.getCategories"
     * @return the list of {@link WordpressCategory}s of the blog.
     * @throws XmlRpcException
     * @throws IOException
     */
    public List<WordpressCategory> getCategories() throws XmlRpcException, IOException {

        Vector<Object> params = new Vector<Object>();

        params.add(wordpressConnection.getBlogId());
        params.add(wordpressConnection.getUsername());
        params.add(wordpressConnection.getPassword());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>)
        client.execute(GET_CATEGORIES_METHOD_NAME, params);

        List<WordpressCategory> categories = new ArrayList<WordpressCategory>(rows.size());
        for (Map<String, Object> row : rows) {
            WordpressCategory category = new WordpressCategory();
            category.setId(Integer.valueOf(row.get("categoryId").toString()));
            category.setParentId(Integer.valueOf(row.get("parentId").toString()));
            category.setDescription((String) row.get("description"));
            category.setCategoryName((String) row.get("categoryName"));
            category.setHtmlUrl((String) row.get("htmlUrl"));
            category.setRssUrl((String) row.get("rssUrl"));
            categories.add(category);
        }

        return categories;
    }


    /**
     * @see "http://codex.wordpress.org/XML-RPC_wp#wp.getTags"
     * @return the list of {@link WordpressCategory}s of the blog.
     * @throws XmlRpcException
     * @throws IOException
     */
    public List<WordpressTag> getTags() throws XmlRpcException, IOException {

        Vector<Object> params = new Vector<Object>();

        params.add(wordpressConnection.getBlogId());
        params.add(wordpressConnection.getUsername());
        params.add(wordpressConnection.getPassword());

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>)
        client.execute(GET_TAGS_METHOD_NAME, params);

        List<WordpressTag> tags = new ArrayList<WordpressTag>(rows.size());
        for (Map<String, Object> row : rows) {
            WordpressTag tag = new WordpressTag();
            tag.setId(Integer.valueOf(row.get("tag_id").toString()));
            tag.setName((String) row.get("name"));
            tag.setCount(Integer.valueOf(row.get("count").toString()));
            tag.setSlug((String) row.get("slug"));
            tag.setHtmlUrl((String) row.get("html_url"));
            tag.setRssUrl((String) row.get("rss_url"));
            tags.add(tag);
        }

        return tags;
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
    public WordpressPost findPostById(int postId) throws XmlRpcException, IOException {
        Vector<Object> params = new Vector<Object>();
        params.add(wordpressConnection.getBlogId());
        params.add(postId);
        params.add(wordpressConnection.getUsername());
        params.add(wordpressConnection.getPassword());
        Map<String, Object> map;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapTemp = (Map<String, Object>) client.execute(GET_PAGE_METHOD_NAME, params);
            map = mapTemp;
        } catch(XmlRpcException e) {
            if(e.getCause() != null && "Unparseable date: \"T::\"".equals(e.getCause().getMessage())){
                //Means WordpressConnection didn't find the post and sent an empty XML back.
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

        WordpressPost post = new WordpressPost();
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
        List<Integer> categoryIds = null;
        if(body.startsWith("<category>")) {
            String categories = StringUtils.substringBetween(body, "<category>", "</category>");
            String[] tokens = categories.split(",");
            categoryIds = new ArrayList<Integer>(tokens.length);
            for (String token : tokens) {
                categoryIds.add(Integer.valueOf(token));
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
    public WordpressPost post(WordpressPost post) throws XmlRpcException, IOException {

        Vector<Object> params = new Vector<Object>();

        params.add(wordpressConnection.getBlogId());
        params.add(wordpressConnection.getUsername());
        params.add(wordpressConnection.getPassword());

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

        WordpressConnection wordpressConnection = new WordpressConnection(url, userName, password, blogId);

        WordpressClient client = new WordpressClient(
            wordpressConnection
            //,"proxy.gicm.net", 3128
            );

        System.out.println(client.getUsers());

        System.out.println(client.getCategories());

        System.out.println(client.getTags());

        WordpressPost post = client.findPostById(40);

        System.out.println(post);

        post = new WordpressPost();
        post.setAuthorId(3);
        post.setTitle("Revue de Presse Xebia");
        post.setBody("coucou c'est un draft");
        post.setCategoryNames(Arrays.asList(new String[]{"test", "newcat"}));//categories must exist.
        post.setTagNames(Arrays.asList(new String[]{"tag1", "tag2", "newtag"})); //tags are dynamically created.
        post = client.post(post);
        System.out.println(post);

    }
}