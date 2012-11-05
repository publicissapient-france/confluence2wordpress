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
package fr.dutra.confluence2wordpress.wp;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlrpc.XmlRpcException;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import fr.dutra.confluence2wordpress.util.CollectionUtils;
import fr.dutra.confluence2wordpress.xmlrpc.CommonsXmlRpcTransportFactory;
import fr.dutra.confluence2wordpress.xmlrpc.XmlRpcClient;

/**
 * 
 * @see "http://codex.wordpress.org/XML-RPC_wp"
 * 
 * @author Alexandre Dutra
 * 
 */
public class WordpressClient {

	private static final String CREATE_POST_METHOD_NAME = "c2w.newPost";

	private static final String UPDATE_POST_METHOD_NAME = "c2w.editPost";

	private static final String FIND_POST_BY_ID_METHOD_NAME = "metaWeblog.getPost"; // "blogger.getPost";

	private static final String GET_USERS_METHOD_NAME = "c2w.getAuthors"; // "wp.getAuthors";

	private static final String GET_CATEGORIES_METHOD_NAME = "wp.getCategories";

	private static final String GET_TAGS_METHOD_NAME = "wp.getTags";

	private static final String UPLOAD_FILE_METHOD_NAME = "c2w.uploadFile"; // "wp.uploadFile";

	private static final String FIND_PAGE_ID_BY_SLUG_METHOD_NAME = "c2w.findPageIdBySlug";

	private static final String PING_METHOD_NAME = "c2w.ping";

	/**
	 * Very, VERY old version of the lib bundled with Confluence. Does not even know about Lists and Maps, only Vectors and Hashtables.
	 * Has bugs related to thread-safety.
	 */
	private final XmlRpcClient client;

	private final WordpressConnection wordpressConnection;

	private final ExecutorService pool;
	
	public WordpressClient(WordpressConnection wordpressConnection) {
		this.wordpressConnection = wordpressConnection;
		CommonsXmlRpcTransportFactory factory;
		URL url = wordpressConnection.getUrl();
		if (this.wordpressConnection.getProxyHost() != null && this.wordpressConnection.getProxyPort() != null) {
			factory = new CommonsXmlRpcTransportFactory(
					url, 
					wordpressConnection.getProxyHost(), 
					wordpressConnection.getProxyPort(), 
					wordpressConnection.getMaxConnections());
		} else {
			factory = new CommonsXmlRpcTransportFactory(url, wordpressConnection.getMaxConnections());
		}
		this.client = new XmlRpcClient(url, factory);
		this.client.setMaxThreads(wordpressConnection.getMaxConnections());
		this.pool = Executors.newFixedThreadPool(
				wordpressConnection.getMaxConnections(), 
				new ThreadFactoryBuilder().setDaemon(true).setNameFormat("wp-client-%s").build());
	}

	public synchronized void destroy(){
		this.pool.shutdownNow();
	}
	
	public String ping(String text) throws WordpressXmlRpcException {
		Vector<Object> params = new Vector<Object>();
		params.add(wordpressConnection.getBlogId());
		params.add(wordpressConnection.getUsername());
		params.add(wordpressConnection.getPassword());
		params.add(text);
		return invoke(PING_METHOD_NAME, params);
	}

	/**
	 * @see "http://codex.wordpress.org/XML-RPC_wp#wp.getAuthors"
	 * @return the list of {@link WordpressUser}s of the blog.
	 * @throws WordpressXmlRpcException
	 */
	public Future<List<WordpressUser>> getUsers() {
		return pool.submit(new Callable<List<WordpressUser>>() {

			@Override
			public List<WordpressUser> call() throws Exception {

				Vector<Object> params = new Vector<Object>();
		
				params.add(wordpressConnection.getBlogId());
				params.add(wordpressConnection.getUsername());
				params.add(wordpressConnection.getPassword());
		
				List<Map<String, Object>> rows = invoke(GET_USERS_METHOD_NAME, params);
		
				List<WordpressUser> users = new ArrayList<WordpressUser>(rows.size());
				for (Map<String, Object> row : rows) {
					WordpressUser user = new WordpressUser();
					user.setId(Integer.valueOf(row.get("user_id").toString()));
					user.setLogin(row.get("user_login").toString());
					user.setDisplayName((String) row.get("display_name"));
					user.setFirstName((String) row.get("first_name"));
					user.setLastName((String) row.get("last_name"));
					user.setNiceName((String) row.get("user_nicename"));
					if (row.get("user_level") != null && StringUtils.isNotEmpty(row.get("user_level").toString())) {
						user.setLevel(Integer.valueOf(row.get("user_level").toString()));
					}
					users.add(user);
				}
		
				return users;
			}
			
		});
	}

	/**
	 * @see "http://codex.wordpress.org/XML-RPC_wp#wp.getCategories"
	 * @return the list of {@link WordpressCategory}s of the blog.
	 * @throws WordpressXmlRpcException
	 */
	public Future<List<WordpressCategory>> getCategories() {
		return pool.submit(new Callable<List<WordpressCategory>>() {

			@Override
			public List<WordpressCategory> call() throws Exception {
				Vector<Object> params = new Vector<Object>();
		
				params.add(wordpressConnection.getBlogId());
				params.add(wordpressConnection.getUsername());
				params.add(wordpressConnection.getPassword());
		
				List<Map<String, Object>> rows = invoke(GET_CATEGORIES_METHOD_NAME, params);
		
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
		});
	}

	/**
	 * @see "http://codex.wordpress.org/XML-RPC_wp#wp.getTags"
	 * @return the list of {@link WordpressCategory}s of the blog.
	 * @throws WordpressXmlRpcException
	 */
	public Future<List<WordpressTag>> getTags() {
		return pool.submit(new Callable<List<WordpressTag>>() {

			@Override
			public List<WordpressTag> call() throws Exception {
				Vector<Object> params = new Vector<Object>();
		
				params.add(wordpressConnection.getBlogId());
				params.add(wordpressConnection.getUsername());
				params.add(wordpressConnection.getPassword());
		
				List<Map<String, Object>> rows = invoke(GET_TAGS_METHOD_NAME, params);
		
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
		});
	}

	/**
	 * @see "http://xmlrpc.free-conversant.com/docs/bloggerAPI#getPost"
	 * @see "http://joysofprogramming.com/blogger-getpost/"
	 * @see "http://stackoverflow.com/questions/3083039/how-can-i-get-a-post-with-xml-rpc-in-wordpress"
	 * 
	 * @param postId
	 * @return post
	 * @throws WordpressXmlRpcException
	 */
	public WordpressPost findPostById(int postId) throws WordpressXmlRpcException {
		Vector<Object> params = new Vector<Object>();
		// params.add(wordpressConnection.getBlogId());
		params.add(postId);
		params.add(wordpressConnection.getUsername());
		params.add(wordpressConnection.getPassword());
		Map<String, Object> map = invoke(FIND_POST_BY_ID_METHOD_NAME, params);
		return convertToPost(map);
	}

	/**
	 * 
	 * @param postSlug
	 * @return postId
	 * @throws WordpressXmlRpcException
	 */
	public Integer findPageIdBySlug(String postSlug) throws WordpressXmlRpcException {
		Vector<Object> params = new Vector<Object>();
		params.add(wordpressConnection.getBlogId());
		params.add(wordpressConnection.getUsername());
		params.add(wordpressConnection.getPassword());
		params.add(postSlug);
		Integer result = (Integer) invoke(FIND_PAGE_ID_BY_SLUG_METHOD_NAME, params);
		if (result != null && result == 0) {
			return null;
		}
		return result;
	}

	/**
	 * 
	 * @see "http://www.xmlrpc.com/metaWeblogApi" 
	 * @see "http://mindsharestrategy.com/wp-xmlrpc-metaweblog/" 
	 * @see "http://www.perkiset.org/forum/perl/metaweblognewpost_to_wordpress_blog_xmlrpcphp-t1307.0.html"
	 * @see "http://joysofprogramming.com/wordpress-xmlrpc-metaweblog-newpost/"
	 * @see "http://life.mysiteonline.org/archives/161-Automatic-Post-Creation-with-Wordpress,-PHP,-and-XML-RPC.html"
	 * 
	 * @param post
	 *            to create
	 * @return the created post
	 * @throws WordpressXmlRpcException
	 */
	public WordpressPost post(WordpressPost post) throws WordpressXmlRpcException {

		Vector<Object> params = new Vector<Object>();

		params.add(wordpressConnection.getBlogId());
		params.add(wordpressConnection.getUsername());
		params.add(wordpressConnection.getPassword());
		if (post.getPostId() != null) {
			params.add(post.getPostId());
		}

		Hashtable<String, Object> map = new Hashtable<String, Object>();
		map.put("title", post.getTitle());
		map.put("description", post.getBody());
		map.put("wp_author_id", post.getAuthorId());
		map.put("wp_slug", post.getPostSlug());
		Date dateCreated = post.getDateCreated();
		if (dateCreated != null) {
			map.put("dateCreated", convertToNaiveUTC(dateCreated));
		}
		if (post.getCategoryNames() != null) {
			map.put("categories", new Vector<String>(post.getCategoryNames()));
		} else {
			map.put("categories", new Vector<String>());
		}
		if (post.getTagNames() != null) {
			map.put("mt_keywords", new Vector<String>(post.getTagNames()));
		} else {
			map.put("mt_keywords", new Vector<String>());
		}
		params.add(map);

		// to publish ?
		params.add(!post.isDraft());

		String methodName;
		if (post.getPostId() == null) {
			methodName = CREATE_POST_METHOD_NAME;
		} else {
			methodName = UPDATE_POST_METHOD_NAME;
		}
		Map<String, Object> ret = invoke(methodName, params);
		return convertToPost(ret);
	}


	/**
	 * @param file
	 * @return
	 * @throws WordpressXmlRpcException
	 */
	public Future<WordpressFile> uploadFile(final WordpressFile file) {

		return pool.submit(new Callable<WordpressFile>() {

			@Override
			public WordpressFile call() throws Exception {
				
				Vector<Object> params = new Vector<Object>();
				params.add(wordpressConnection.getBlogId());
				params.add(wordpressConnection.getUsername());
				params.add(wordpressConnection.getPassword());

				Hashtable<String, Object> map = new Hashtable<String, Object>();
				map.put("name", file.getFileName());
				map.put("type", file.getMimeType());
				map.put("bits", file.getData());
				// see http://core.trac.wordpress.org/ticket/17604
				map.put("overwrite", true);
				params.add(map);

				Map<String, ?> response = invoke(UPLOAD_FILE_METHOD_NAME, params);

				file.setFileName((String) response.get("file"));
				file.setMimeType((String) response.get("type"));
				file.setUrl((String) response.get("url"));

				String baseUrl = StringUtils.substringBeforeLast(file.getUrl(), "/");

				if (response.get("meta") != null && response.get("meta") instanceof Map) {
					@SuppressWarnings("unchecked")
					Map<String, ?> meta = (Map<String, ?>) response.get("meta");
					if (meta != null) {
						if (meta.containsKey("height")) {
							file.setHeight((Integer) meta.get("height"));
						}
						if (meta.containsKey("width")) {
							file.setWidth((Integer) meta.get("width"));
						}
						@SuppressWarnings("unchecked")
						Map<String, ?> sizes = (Map<String, ?>) meta.get("sizes");
						if (sizes != null) {
							for (Entry<String, ?> entry : sizes.entrySet()) {
								@SuppressWarnings("unchecked")
								Map<String, ?> value = (Map<String, ?>) entry.getValue();
								WordpressFile alternative = createAlternative(value, file.getMimeType(), baseUrl);
								file.putAlternative(entry.getKey(), alternative);
							}
						}
					}
				}
				return file;
			}
		});
		

	}

	private WordpressFile createAlternative(Map<String, ?> value, String mimeType, String baseUrl) {
		WordpressFile alternative = new WordpressFile((String) value.get("file"));
		alternative.setAlternative(true);
		alternative.setMimeType(mimeType);
		String fileName = (String) value.get("file");
		alternative.setFileName(fileName);
		alternative.setUrl(baseUrl + "/" + fileName);
		if (value.containsKey("height")) {
			alternative.setHeight((Integer) value.get("height"));
		}
		if (value.containsKey("width")) {
			alternative.setWidth((Integer) value.get("width"));
		}
		return alternative;
	}

	private WordpressPost convertToPost(Map<String, Object> map) {

		WordpressPost post = new WordpressPost();

		Object postId = map.get("postid");
		post.setPostId(Integer.valueOf(postId.toString()));
		post.setDraft(false);

		Object authorId = map.get("wp_author_id");
		post.setAuthorId(authorId == null ? null : Integer.valueOf(authorId.toString()));

		Date dateUtc = (Date) map.get("dateCreated");
		post.setDateCreated(convertFromNaiveUTC(dateUtc));

		StringBuilder body = new StringBuilder();
		if (map.get("description") != null) {
			body.append((String) map.get("description"));
		}
		if (map.get("mt_text_more") != null) {
			//tricky: if the post is saved on wordpress side,
			//even without modification, wordpress will append an "\n" before the "more" tag
			//but if the post has never been edited on wordpress,
			//then the "\n" is missing
			if(body.charAt(body.length()-1) != '\n'){
				body.append("\n");
			}
			body.append("<!--more-->");
			body.append((String) map.get("mt_text_more"));
		}
		post.setBody(body.toString());

		String title = (String) map.get("title");
		post.setTitle(title);

		post.setDraft(!"publish".equals(map.get("post_status")));

		@SuppressWarnings("unchecked")
		List<String> categoryNames = (List<String>) map.get("categories");
		post.setCategoryNames(new ArrayList<String>(categoryNames));

		//optional field
		List<String> tagNames = CollectionUtils.split((String) map.get("mt_keywords"), ",");
		if(tagNames != null) {
			post.setTagNames(new ArrayList<String>(tagNames));
		}

		String slug = (String) map.get("wp_slug");
		post.setPostSlug(slug);

		String permaLink = (String) map.get("permaLink");
		post.setLink(permaLink);

		return post;
	}

	/**
	 * Date values are sent with no time zone information;
	 * Wordpress assumes they are in UTC.
	 * Hence the need to convert to a fake date bearing the correct information,
	 * but in a wrong time zone.
	 * @param date
	 * @return
	 */
	private Date convertToNaiveUTC(Date date) {
		TimeZone zone = TimeZone.getDefault();
		long time = date.getTime();
		return new Date(time - zone.getOffset(time));
	}
	
	/**
	 * Date values are received with no time zone information;
	 * Wordpress assumes they are in UTC.
	 * Hence the need to convert from a fake date bearing the correct information,
	 * but in a wrong time zone.
	 * @param date
	 * @return
	 */
	private Date convertFromNaiveUTC(Date date){
		TimeZone zone = TimeZone.getDefault();
		long time = date.getTime();
		return new Date(time + zone.getOffset(time));
	}
	
	@SuppressWarnings("unchecked")
	private <T> T invoke(String methodName, Vector<Object> params) throws WordpressXmlRpcException {
		try {
			return (T) client.execute(methodName, params);
		} catch (XmlRpcException e) {
			throw new WordpressXmlRpcException("Error invoking method: " + methodName + ": " + e.getMessage(), e);
		} catch (IOException e) {
			throw new WordpressXmlRpcException("Error invoking method: " + methodName + ": " + e.getMessage(), e);
		}
	}

}