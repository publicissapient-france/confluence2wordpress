/**
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
/**
 * 
 */
package fr.dutra.confluence2wordpress.core.author;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import fr.dutra.confluence2wordpress.util.UrlUtils;



public class AuthorURL {
	
	private final String url;
	
	private final String icon;

	public AuthorURL(String url) throws MalformedURLException, URISyntaxException {
		this(url, pickIcon(url));
	}

	private static String pickIcon(String url) {
		if(url.contains("xebia")) return "xebia";
		if(url.contains("aim")) return "aim";
		if(url.contains("android")) return "android";
		if(url.contains("apple")) return "apple";
		if(url.contains("ask")) return "ask";
		if(url.contains("bebo")) return "bebo";
		if(url.contains("behance")) return "behance";
		if(url.contains("blogger")) return "blogger";
		if(url.contains("delicious")) return "delicious";
		if(url.contains("designbump")) return "designbump";
		if(url.contains("designfloat")) return "designfloat";
		if(url.contains("designmoo")) return "designmoo";
		if(url.contains("deviantart")) return "deviantart";
		if(url.contains("digg")) return "digg";
		if(url.contains("dribbble")) return "dribbble";
		if(url.contains("mailto")) return "email";
		if(url.contains("evernote")) return "evernote";
		if(url.contains("facebook")) return "facebook";
		if(url.contains("flickr")) return "flickr";
		if(url.contains("foursquare")) return "foursquare";
		if(url.contains("friendfeed")) return "friendfeed";
		if(url.contains("github")) return "github";
		if(url.contains("plus.google.com")) return "gplus";
		if(url.contains("google")) return "google";
		if(url.contains("googletalk") || url.contains("gtalk")) return "googletalk";
		if(url.contains("hackernews")) return "hackernews";
		if(url.contains("icq")) return "icq";
		if(url.contains("ios")) return "ios";
		if(url.contains("lastfm")) return "lastfm";
		if(url.contains("linkedin")) return "linkedin";
		if(url.contains("meetup")) return "meetup";
		if(url.contains("mobileme")) return "mobileme";
		if(url.contains("myspace")) return "myspace";
		if(url.contains("netvibes")) return "netvibes";
		if(url.contains("newsvine")) return "newsvine";
		if(url.contains("ning")) return "ning";
		if(url.contains("orkut")) return "orkut";
		if(url.contains("picasa")) return "picasa";
		if(url.contains("pinterest")) return "pinterest";
		if(url.contains("posterous")) return "posterous";
		if(url.contains("reddit")) return "reddit";
		if(url.contains("rss") || url.contains("atom")) return "rss";
		if(url.contains("sharethis")) return "sharethis";
		if(url.contains("skype")) return "skype";
		if(url.contains("slashdot")) return "slashdot";
		if(url.contains("slideshare")) return "slideshare";
		if(url.contains("squidoo")) return "squidoo";
		if(url.contains("stumbleupon")) return "stumbleupon";
		if(url.contains("technorati")) return "technorati";
		if(url.contains("tumblr")) return "tumblr";
		if(url.contains("twitter")) return "twitter";
		if(url.contains("vimeo")) return "vimeo";
		if(url.contains("windows")) return "windows";
		if(url.contains("wordpress")) return "wordpress";
		if(url.contains("yahoo")) return "yahoo";
		if(url.contains("yelp")) return "yelp";
		if(url.contains("youtube")) return "youtube";
		return "generic";
	}

	public AuthorURL(String url, String icon) throws MalformedURLException, URISyntaxException {
		this.url = UrlUtils.sanitize(url);
		this.icon = icon;
	}

	public String getUrl() {
		return url;
	}

	public String getIcon() {
		return icon;
	}

	@Override
	public String toString() {
		return url;
	}

}
