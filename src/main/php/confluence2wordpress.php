<?php
/*
Plugin Name: Confluence XML-RPC Tools
Plugin URI: https://github.com/adutra/confluence2wordpress
Description: This Wordpress plugin brings additional XML-RPC APIs required for interaction with the "confluence2wordpress" Confluence plugin.
Version: ${project.version}
Author: Alexandre Dutra
Author URI: https://github.com/adutra
License: Apache License v. 2
*/

/*
Copyright 2011 Alexandre Dutra

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

require_once(ABSPATH . 'wp-admin/includes/plugin.php');

add_filter('xmlrpc_methods', 'add_confluence2wordpress_xmlrpc_methods');

function add_confluence2wordpress_xmlrpc_methods( $methods ) {
    $methods['confluence2wordpress.getAuthors'] = 'get_authors';
    return $methods;
}

function get_authors($args) {
	
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

?>