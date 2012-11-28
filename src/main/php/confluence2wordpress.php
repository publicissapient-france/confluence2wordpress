<?php
/*
 * Copyright 2011-2012 Alexandre Dutra
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
/*
Plugin Name: Confluence to Wordpress Synchronizer Tools
Plugin URI: https://github.com/adutra/confluence2wordpress
Description: This Wordpress plugin brings several features required for interaction with the "Confluence to Wordpress Synchronizer" plugin for Confluence, as well as some useful macros, like [permalink] or [author].
Version: ${project.version}
Author: Alexandre Dutra
Author URI: https://github.com/adutra
License: Apache License v. 2
*/

require_once(ABSPATH . 'wp-admin/includes/plugin.php');
require_once(ABSPATH . 'wp-includes/post.php');
require_once(ABSPATH . 'wp-includes/link-template.php');

add_filter('xmlrpc_methods', 'c2w_add_xmlrpc_methods');
add_filter('upload_mimes', 'c2w_add_mime_types');
 
add_action( 'wp_enqueue_scripts', 'c2w_enqueue_scripts' );

add_shortcode( 'permalink', 'c2w_generate_permalink' );
add_shortcode( 'author', 'c2w_generate_author' );

/**
 * Register CSS and JS files.
 */
function c2w_enqueue_scripts() {
	wp_register_style( 'c2w-author', plugins_url('css/author.css', __FILE__) );
	wp_register_style( 'c2w-toc', plugins_url('css/toc.css', __FILE__) );
	wp_enqueue_style( 'c2w-author' );
	wp_enqueue_style( 'c2w-toc' );
}

/**
 * Register new XML-RPC methods.
 * @param array $methods
 * @return array
 */
function c2w_add_xmlrpc_methods( $methods ) {
    $methods['c2w.ping'] = 'c2w_ping';
    $methods['c2w.newPost'] = 'c2w_new_post';
    $methods['c2w.editPost'] = 'c2w_edit_post';
    $methods['c2w.getAuthors'] = 'c2w_get_authors';
    $methods['c2w.findPageIdBySlug'] = 'c2w_get_page_id_by_slug';
    $methods['c2w.uploadFile'] = 'c2w_upload_file';
    return $methods;
}

/**
 * Adds additional mime types to allowed mime types for uploaded files.
 * @see functions.php function wp_check_filetype()
 * @param array $mimes
 */
function c2w_add_mime_types( $mimes ) {
    $mimes['xml'] = 'text/xml';
    return $mimes;
}

/**
 * Register shortcode for [permalink] macros.
 * @param array $atts macro attributes (ignored)
 * @return the current post permalink.
 */
function c2w_generate_permalink( $atts ){
	global $post;
	return get_permalink($post->ID);
}

/**
 * Register shortcode for [author] macros.
 * @param array $atts macro attributes
 * @return the macros's rendered result.
 */
function c2w_generate_author( $atts ) {
	extract( shortcode_atts( array(
		'firstname' => NULL,
		'lastname' => NULL,
		'gravatar' => NULL,
		'username' => NULL,
		'twitter' => NULL,
		'urls' => NULL
	), $atts ));
	
	$parsedUrls = array();
	if($username) {
		$parsedUrls[] = array("xebia", "http://blog.xebia.fr/author/" . urlencode(html_entity_decode($username)));
	}
	if($twitter) {
		$parsedUrls[] = array("twitter", "http://twitter.com/" . urlencode(html_entity_decode($twitter)));
	}
	$urls = preg_split("/[\s,]+/", $urls);
	foreach ($urls as $url) {
		$url = trim($url);
		if(!$url) continue;
			 if(strpos($url, "xebia") !== false) $parsedUrls[] = array("xebia", $url);
		else if(strpos($url, "aim") !== false) $parsedUrls[] = array("aim", $url);
		else if(strpos($url, "android") !== false) $parsedUrls[] = array("android", $url);
		else if(strpos($url, "apple") !== false) $parsedUrls[] = array("apple", $url);
		else if(strpos($url, "ask") !== false) $parsedUrls[] = array("ask", $url);
		else if(strpos($url, "bebo") !== false) $parsedUrls[] = array("bebo", $url);
		else if(strpos($url, "behance") !== false) $parsedUrls[] = array("behance", $url);
		else if(strpos($url, "blogger") !== false) $parsedUrls[] = array("blogger", $url);
		else if(strpos($url, "delicious") !== false) $parsedUrls[] = array("delicious", $url);
		else if(strpos($url, "designbump") !== false) $parsedUrls[] = array("designbump", $url);
		else if(strpos($url, "designfloat") !== false) $parsedUrls[] = array("designfloat", $url);
		else if(strpos($url, "designmoo") !== false) $parsedUrls[] = array("designmoo", $url);
		else if(strpos($url, "deviantart") !== false) $parsedUrls[] = array("deviantart", $url);
		else if(strpos($url, "digg") !== false) $parsedUrls[] = array("digg", $url);
		else if(strpos($url, "dribbble") !== false) $parsedUrls[] = array("dribbble", $url);
		else if(strpos($url, "mailto") !== false) $parsedUrls[] = array("email", $url);
		else if(strpos($url, "evernote") !== false) $parsedUrls[] = array("evernote", $url);
		else if(strpos($url, "facebook") !== false) $parsedUrls[] = array("facebook", $url);
		else if(strpos($url, "flickr") !== false) $parsedUrls[] = array("flickr", $url);
		else if(strpos($url, "foursquare") !== false) $parsedUrls[] = array("foursquare", $url);
		else if(strpos($url, "friendfeed") !== false) $parsedUrls[] = array("friendfeed", $url);
		else if(strpos($url, "github") !== false) $parsedUrls[] = array("github", $url);
		else if(strpos($url, "plus.google.com") !== false) $parsedUrls[] = array("gplus", $url);
		else if(strpos($url, "google") !== false) $parsedUrls[] = array("google", $url);
		else if(strpos($url, "googletalk") || strpos($url, "gtalk") !== false) $parsedUrls[] = array("googletalk", $url);
		else if(strpos($url, "hackernews") !== false) $parsedUrls[] = array("hackernews", $url);
		else if(strpos($url, "icq") !== false) $parsedUrls[] = array("icq", $url);
		else if(strpos($url, "ios") !== false) $parsedUrls[] = array("ios", $url);
		else if(strpos($url, "lastfm") !== false) $parsedUrls[] = array("lastfm", $url);
		else if(strpos($url, "linkedin") !== false) $parsedUrls[] = array("linkedin", $url);
		else if(strpos($url, "meetup") !== false) $parsedUrls[] = array("meetup", $url);
		else if(strpos($url, "mobileme") !== false) $parsedUrls[] = array("mobileme", $url);
		else if(strpos($url, "myspace") !== false) $parsedUrls[] = array("myspace", $url);
		else if(strpos($url, "netvibes") !== false) $parsedUrls[] = array("netvibes", $url);
		else if(strpos($url, "newsvine") !== false) $parsedUrls[] = array("newsvine", $url);
		else if(strpos($url, "ning") !== false) $parsedUrls[] = array("ning", $url);
		else if(strpos($url, "orkut") !== false) $parsedUrls[] = array("orkut", $url);
		else if(strpos($url, "picasa") !== false) $parsedUrls[] = array("picasa", $url);
		else if(strpos($url, "pinterest") !== false) $parsedUrls[] = array("pinterest", $url);
		else if(strpos($url, "posterous") !== false) $parsedUrls[] = array("posterous", $url);
		else if(strpos($url, "reddit") !== false) $parsedUrls[] = array("reddit", $url);
		else if(strpos($url, "rss") !== false || strpos($url, "atom") !== false) $parsedUrls[] = array("rss", $url);
		else if(strpos($url, "sharethis") !== false) $parsedUrls[] = array("sharethis", $url);
		else if(strpos($url, "skype") !== false) $parsedUrls[] = array("skype", $url);
		else if(strpos($url, "slashdot") !== false) $parsedUrls[] = array("slashdot", $url);
		else if(strpos($url, "slideshare") !== false) $parsedUrls[] = array("slideshare", $url);
		else if(strpos($url, "squidoo") !== false) $parsedUrls[] = array("squidoo", $url);
		else if(strpos($url, "stackoverflow") !== false) $parsedUrls[] = array("stackoverflow", $url);
		else if(strpos($url, "stumbleupon") !== false) $parsedUrls[] = array("stumbleupon", $url);
		else if(strpos($url, "technorati") !== false) $parsedUrls[] = array("technorati", $url);
		else if(strpos($url, "tumblr") !== false) $parsedUrls[] = array("tumblr", $url);
		else if(strpos($url, "twitter") !== false) $parsedUrls[] = array("twitter", $url);
		else if(strpos($url, "vimeo") !== false) $parsedUrls[] = array("vimeo", $url);
		else if(strpos($url, "windows") !== false) $parsedUrls[] = array("windows", $url);
		else if(strpos($url, "wordpress") !== false) $parsedUrls[] = array("wordpress", $url);
		else if(strpos($url, "yahoo") !== false) $parsedUrls[] = array("yahoo", $url);
		else if(strpos($url, "yelp") !== false) $parsedUrls[] = array("yelp", $url);
		else if(strpos($url, "youtube") !== false) $parsedUrls[] = array("youtube", $url);
		else $parsedUrls[] = array("generic", $url);
	}

	$result = '<div class="c2w-author">';
	
	$result .= '<div class="c2w-author-icons">';
	
	if ($gravatar) {
		$hash = md5(strtolower(trim($gravatar)));
		$gravatarProfileUrl = c2w_follow_redirects("http://www.gravatar.com/$hash");
		//image in HTTPS to avoid browser warnings
		$gravatarImageUrl = "https://secure.gravatar.com/avatar/$hash.jpg";
		$result .= "<a class='c2w-author-icon c2w-author-gravatar' style='background-image:url($gravatarImageUrl?s=24)' href='$gravatarProfileUrl' target='_blank'><img title='$gravatarProfileUrl' src='$gravatarImageUrl?s=24' alt='$gravatarProfileUrl' width='24' height='24' /></a>";
	}
	
	foreach ($parsedUrls as $url) {
		$result .= "<a class='c2w-author-icon' href='$url[1]' target='_blank'><img title='$url[1]' src='" . WP_PLUGIN_URL . '/' . basename(dirname(__FILE__)) . "/img/rkc-social-set/24x24/24x24-${url[0]}.png' alt='$url[1]' width='24' height='24' /></a>";
	}
	$result .= '</div>';
	
	$result .= "<span class='c2w-author-name'>Par ";
	if($parsedUrls[0]) {
		$result .= "<a href='" . $parsedUrls[0][1] ."' target='_blank'>$firstname $lastname</a>";
	} else {
		$result .= "$firstname $lastname";
	}
	$result .= "</span></div>";
	return $result;
}

/**
 * Follow redirections.
 * @see http://codex.wordpress.org/HTTP_API
 * @param string $url the initial url
 * @param int $max_redirects the maximu number of redirections to follow
 * @return the resolved URL
 */
function c2w_follow_redirects($url, $max_redirects = 5) {
	$headers = array();
	//we set the same accept-language header as the client request,
	//because URLs returned by Gravatar can vary according to request locale
	if(!empty($_SERVER['HTTP_ACCEPT_LANGUAGE'])){
		$headers['accept-language'] = $_SERVER['HTTP_ACCEPT_LANGUAGE'];
	}
	$args = array(
		'method' => 'HEAD',
		'timeout' => apply_filters( 'http_request_timeout', 5),
		'redirection' => 0,
		'httpversion' => apply_filters( 'http_request_version', '1.0'),
		'user-agent' => apply_filters( 'http_headers_useragent', 'WordPress/' . $wp_version . '; ' . get_bloginfo( 'url' )  ),
		'blocking' => true,
		'headers' => $headers,
		'cookies' => array(),
		'body' => null,
		'compress' => false,
		'decompress' => true,
		'sslverify' => true,
		'stream' => false,
		'filename' => null
	);
	for($i = 0; $i < $max_redirects; $i++) {
		$response = wp_remote_head($url, $args);
		if ( ! is_wp_error( $response ) ) {
			$response_code = wp_remote_retrieve_response_code( $response );
			if($response_code == '301' || $response_code == '302') {
				$location = wp_remote_retrieve_header( $response, 'location' );
				if( $location != '' ) {
					$url = WP_HTTP::make_absolute_url( $location, $url );
					continue;
				}
			}
		}
		break;
	}
	return $url;
}

/**
 * Ping method (useful to test the connection between Confluence and Wordpress).
 * @param array $args
 * @return IXR_Error|string
 */
function c2w_ping($args){
	// Parse the arguments, assuming they're in the correct order
	
	//please see http://core.trac.wordpress.org/ticket/10513
	//WP version MUST be >= 2.9
	global $wp_xmlrpc_server;

	$wp_xmlrpc_server->escape($args);

	$blog_id	= (int) $args[0];
	$username	= $args[1];
	$password	= $args[2];
	$text	    = $args[3];
	
    // Let's run a check to see if credentials are okay
	if ( !$user = $wp_xmlrpc_server->login($username, $password) ) {
		return $wp_xmlrpc_server->error;
	}
	
	if(!current_user_can("edit_posts")) {
		return(new IXR_Error(401, __("Sorry, you cannot access this API.")));
	}
	
	return $text;
}

function c2w_new_post ($args) {
	
	global $wp_xmlrpc_server;
	
	$blog_ID  = $args[0];
	$username = $args[1];
	$password = $args[2];
	$content_struct = $args[3];
	$publish  = $args[4];
	
	$post_ID  = $wp_xmlrpc_server->mw_newPost(array($blog_ID, $username, $password, $content_struct, $publish));
	
	if ( is_wp_error( $post_ID ) )
			return new IXR_Error(500, $post_ID->get_error_message());
	if ( !$post_ID )
		return new IXR_Error(500, __('Could not create post.'));
	
	$post = $wp_xmlrpc_server->mw_getPost(array($post_ID, $username, $password));	
	return $post;
}

function c2w_edit_post ($args) {
	
	global $wp_xmlrpc_server;
	
	$blog_ID  = $args[0];
	$username = $args[1];
	$password = $args[2];
	$post_ID  = (int) $args[3];
	$content_struct = $args[4];
	$publish  = $args[5];
	
	$result = $wp_xmlrpc_server->mw_editPost(array($post_ID, $username, $password, $content_struct, $publish));
	if ( is_wp_error( $result ) )
			return new IXR_Error(500, $result->get_error_message());
	if ( !$result )
		return new IXR_Error(500, __('Could not update post.'));
	
	$post = $wp_xmlrpc_server->mw_getPost(array($post_ID, $username, $password));	
	return $post;
}


/**
 * Find a page ID by its post slug.
 * Return null if post slug does not exist.
 * @param array $args
 * @return IXR_Error|number the page ID
 */
function c2w_get_page_id_by_slug($args){
	// Parse the arguments, assuming they're in the correct order
	
	//please see http://core.trac.wordpress.org/ticket/10513
	//WP version MUST be >= 2.9
	global $wp_xmlrpc_server;

	$wp_xmlrpc_server->escape($args);

	$blog_id	= (int) $args[0];
	$username	= $args[1];
	$password	= $args[2];
	$slug	    = $args[3];
	
    // Let's run a check to see if credentials are okay
	if ( !$user = $wp_xmlrpc_server->login($username, $password) ) {
		return $wp_xmlrpc_server->error;
	}
	
	if(!current_user_can("edit_posts")) {
		return(new IXR_Error(401, __("Sorry, you cannot access this API.")));
	}

	$page = get_page_by_path($slug, OBJECT, 'post');
	
	if($page){
		return (int) $page->ID;
	}
	
	return (int) null;
}

/**
 * Enhanced version of built-in method "wp.getAuthors".
 * @param array $args
 */
function c2w_get_authors($args) {
	
	// Parse the arguments, assuming they're in the correct order
	
	//please see http://core.trac.wordpress.org/ticket/10513
	//WP version MUST be >= 2.9
	global $wp_xmlrpc_server;

	$wp_xmlrpc_server->escape($args);

	$blog_id	= (int) $args[0];
	$username	= $args[1];
	$password	= $args[2];
	
    // Let's run a check to see if credentials are okay
	if ( !$user = $wp_xmlrpc_server->login($username, $password) ) {
		return $wp_xmlrpc_server->error;
	}

	if(!current_user_can("edit_posts")) {
		return(new IXR_Error(401, __("Sorry, you cannot access this API.")));
	}

	global $wpdb;
	
	//http://codex.wordpress.org/Roles_and_Capabilities#User_Level_to_Role_Conversion
	$sql = 
		"SELECT $wpdb->users.ID as user_id, user_login, user_nicename, display_name, ".
		"(SELECT meta_value from $wpdb->usermeta where $wpdb->usermeta.user_id = $wpdb->users.ID and $wpdb->usermeta.meta_key = 'first_name') as first_name, ".
		"(SELECT meta_value from $wpdb->usermeta where $wpdb->usermeta.user_id = $wpdb->users.ID and $wpdb->usermeta.meta_key = 'last_name') as last_name, ".
		"(SELECT meta_value from $wpdb->usermeta where $wpdb->usermeta.user_id = $wpdb->users.ID and $wpdb->usermeta.meta_key = 'wp_user_level') as user_level ".
		"FROM $wpdb->users" ;
	$rows = $wpdb->get_results($sql);
	
	$editors = array();
	foreach( (array) $rows as $row ) {
		$editors[] = array(
			"user_id"       => $row->user_id,
			"user_login"    => $row->user_login,
			"user_nicename" => $row->user_nicename,
			"display_name"  => $row->display_name,
			"first_name"    => $row->first_name,
			"last_name"     => $row->last_name,
			"user_level"    => $row->user_level
		);
	}

	return($editors);

}

/**
 * This is a copy of built-in method "mw_newMediaObject" in file class-wp-xmlrpc-server.php
 * that fixes the bug described here:
 * http://core.trac.wordpress.org/ticket/17604
 */
function c2w_upload_file($args) {
		//please see http://core.trac.wordpress.org/ticket/10513
		//WP version MUST be >= 2.9
		global $wp_xmlrpc_server;

		global $wpdb;

		$blog_ID     = (int) $args[0];
		$username  = $wpdb->escape($args[1]);
		$password   = $wpdb->escape($args[2]);
		$data        = $args[3];

		$name = sanitize_file_name( $data['name'] );
		$type = $data['type'];
		$bits = $data['bits'];

		logIO('O', '(MW) Received '.strlen($bits).' bytes');

		if ( !$user = $wp_xmlrpc_server->login($username, $password) )
			return $wp_xmlrpc_server->error;

		if ( !current_user_can('upload_files') ) {
			logIO('O', '(MW) User does not have upload_files capability');
			$wp_xmlrpc_server->error = new IXR_Error(401, __('You are not allowed to upload files to this site.'));
			return $wp_xmlrpc_server->error;
		}

		if ( $upload_err = apply_filters( 'pre_upload_error', false ) )
			return new IXR_Error(500, $upload_err);

		if ( !empty($data['overwrite']) && ($data['overwrite'] == true) ) {
			// Get postmeta info on the object.
			$old_file = $wpdb->get_row("
				SELECT ID
				FROM {$wpdb->posts}
				WHERE post_title = '{$name}'
					AND post_type = 'attachment'
			");

			// Delete previous file.
			wp_delete_attachment($old_file->ID);

		}

		$upload = wp_upload_bits($name, NULL, $bits);
		if ( ! empty($upload['error']) ) {
			$errorString = sprintf(__('Could not write file %1$s (%2$s)'), $name, $upload['error']);
			logIO('O', '(MW) ' . $errorString);
			return new IXR_Error(500, $errorString);
		}
		// Construct the attachment array
		// attach to post_id 0
		$post_id = 0;
		$attachment = array(
			'post_title' => $name,
			'post_content' => '',
			'post_type' => 'attachment',
			'post_parent' => $post_id,
			'post_mime_type' => $type,
			'guid' => $upload[ 'url' ]
		);

		// Save the data
		$id = wp_insert_attachment( $attachment, $upload[ 'file' ], $post_id );
		$attachement_metadata = wp_generate_attachment_metadata( $id, $upload['file'] );
		wp_update_attachment_metadata( $id, $attachement_metadata );

		return apply_filters( 'wp_handle_upload', array( 'file' => $name, 'url' => $upload[ 'url' ], 'type' => $type, 'meta' =>  $attachement_metadata), 'upload' );
	}

?>