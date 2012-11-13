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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import fr.dutra.confluence2wordpress.util.CodecUtils;
import fr.dutra.confluence2wordpress.util.UrlUtils;



public class Author {
	
	private final String firstName;
	
	private final String lastName;

	private final String gravatarEmail;
	
	private final String wordpressUsername;

	private final String twitterAccount;

	private final AuthorURL wordpressUrl;

	private final AuthorURL twitterUrl;
	
	private final List<AuthorURL> otherUrls;

	private final List<AuthorURL> allUrls;

	private final String gravatarProfileUrl;

	private final String gravatarImageUrl;

	private static final Splitter SPLITTER = Splitter.on(CharMatcher.WHITESPACE.or(CharMatcher.is(','))).omitEmptyStrings().trimResults();
	
	public static Author fromMacroParameters(Map<String, String> paramMap) throws MalformedURLException, URISyntaxException {
		String firstName = paramMap.get(AuthorMacroParameters.firstName.name());
		String lastName = paramMap.get(AuthorMacroParameters.lastName.name());
		String gravatarEmail = paramMap.get(AuthorMacroParameters.gravatarEmail.name());
		String wordpressUsername = paramMap.get(AuthorMacroParameters.wordpressUsername.name());
		String twitterAccount = paramMap.get(AuthorMacroParameters.twitterAccount.name());
		String others = paramMap.get(AuthorMacroParameters.others.name());
		return new Author(firstName, lastName, gravatarEmail, wordpressUsername, twitterAccount, others == null ? null : SPLITTER.split(others));
	}
	
	public Author(String firstName, String lastName, String gravatarEmail, String wordpressUsername, String twitterAccount, Iterable<String> others) throws MalformedURLException, URISyntaxException {
		this.firstName = firstName;
		this.lastName = lastName;
		this.gravatarEmail = gravatarEmail;
		this.allUrls = new ArrayList<AuthorURL>();
		if(gravatarEmail != null) {
			String hash = CodecUtils.gravatarHash(gravatarEmail);
			//let's follow redirections until we have a pretty nice URL
			gravatarProfileUrl = UrlUtils.followRedirects("http://www.gravatar.com/" + hash, 10);
			//image in HTTPS to avoid browser warnings
			gravatarImageUrl = "https://secure.gravatar.com/avatar/" + hash +".jpg";
		} else {
			gravatarProfileUrl = null;
			gravatarImageUrl = null;
		}
		AuthorURL wordpressUrl = null;
		if(wordpressUsername != null) {
			wordpressUrl = new AuthorURL("http://blog.xebia.fr/author/" + wordpressUsername, "xebia");
			allUrls.add(wordpressUrl);
		}
		this.wordpressUsername = wordpressUsername;
		this.wordpressUrl = wordpressUrl;
		AuthorURL twitterUrl = null;
		if(twitterAccount != null) {
			twitterUrl = new AuthorURL("http://twitter.com/" + twitterAccount, "twitter");
			allUrls.add(twitterUrl);
		}
		this.twitterAccount = twitterAccount;
		this.twitterUrl = twitterUrl;
		this.otherUrls = new ArrayList<AuthorURL>();
		if(others != null) {
			for (String url : others) {
				this.otherUrls.add(new AuthorURL(url));
			}
			this.allUrls.addAll(this.otherUrls);
		}
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getWordpressUsername() {
		return wordpressUsername;
	}

	public String getTwitterAccount() {
		return twitterAccount;
	}

	public List<AuthorURL> getOtherUrls() {
		return otherUrls;
	}

	public AuthorURL getWordpressUrl() {
		return wordpressUrl;
	}

	public AuthorURL getTwitterUrl() {
		return twitterUrl;
	}

	public List<AuthorURL> getAllUrls() {
		return allUrls;
	}

	public String getGravatarEmail() {
		return gravatarEmail;
	}

	public String getGravatarProfileUrl() {
		return gravatarProfileUrl;
	}

	public String getGravatarImageUrl() {
		return gravatarImageUrl;
	}

}
