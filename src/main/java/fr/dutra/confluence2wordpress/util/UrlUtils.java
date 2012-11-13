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
package fr.dutra.confluence2wordpress.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class UrlUtils {

    private static final URI ROOT;
    
    static {
        URI root = null;
        try {
            root = new URI("/");
        } catch (URISyntaxException e) {
        }
        ROOT = root;
    }
    
    /**
     * Extracts the URL part relative to Confluence's root URL, given a variety of input formats, e.g.:
     * <ol>
     * <li><code>/confluence/download/attachments/983042/image.png?version=1&amp;modificationDate=1327402370523</code> (image)</li>
     * <li><code>/confluence/download/thumbnails/983042/image.png</code> (thumbnail)</li>
     * <li><code>http://localhost:1990/confluence/download/attachments/983042/image.png</code> (image)</li>
     * <li><code>/confluence/download/attachments/983042/pom.xml?version=1&amp;modificationDate=1327402370710</code> (attachment)</li>
     * <li><code>/confluence/download/attachments/983042/armonia.png?version=1&amp;modificationDate=1327402370523</code> (image as attachment)</li>
     * </ol>
     */
    public static String extractConfluenceRelativePath(String url, String confluenceRootUrl) {
        try {
            URL context = new URL(confluenceRootUrl);
            URL absolute = new URL(context, url);
            URI relative = context.toURI().relativize(absolute.toURI());
            URI confluencePath = ROOT.resolve(relative);
            return confluencePath.getPath();
        } catch (MalformedURLException e) {
            return url;
        } catch (URISyntaxException e) {
            return url;
        }
    }

    public static String absolutize(String url, String confluenceRootUrl) {
    	try {
			new URL(url);
			//already absolute
			return url;
		} catch (MalformedURLException e) {
		}
        try {
            URL context = new URL(confluenceRootUrl);
            String contextPath = context.getPath();
        	if( ! url.startsWith(contextPath)) {
        		//the url does NOT start with context path: it is meant to be
        		//relative to the context
        		url = contextPath + url;
        	}
            URL absolute = new URL(context, url);
            return absolute.toURI().normalize().toString();
        } catch (MalformedURLException e) {
            return url;
        } catch (URISyntaxException e) {
            return url;
        }
    }
    
    /**
     * Sanitizes the given URL by escaping the path and query parameters, if necessary.
     * @see "http://stackoverflow.com/questions/724043/http-url-address-encoding-in-java"
     * @param url
     * @return
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public static String sanitize(String url) throws URISyntaxException, MalformedURLException {
		URL temp = new URL(url);
		URI uri = new URI(
		    temp.getProtocol(), 
		    temp.getUserInfo(),
		    temp.getHost(),
		    temp.getPort(),
		    temp.getPath(),
		    temp.getQuery(),
		    temp.getRef());
		return uri.toASCIIString();
    }
    
    /**
     * Follow redirections until a final URL is found.
     * @see "http://workbench.cadenhead.org/news/3358/following-web-page-redirects-java"
     * @param url
     * @return
     */
    public static String followRedirects(String url, int maxRedirections) {
        String response = url;
        HttpClient client = new HttpClient();
        DefaultHttpMethodRetryHandler handler = new DefaultHttpMethodRetryHandler(1, false);
        client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, handler);
        HttpMethod method = new HeadMethod(url);
        method.setFollowRedirects(false);
        try {
            int statusCode = client.executeMethod(method);
            if ((statusCode == HttpStatus.SC_MOVED_PERMANENTLY) | (statusCode == HttpStatus.SC_MOVED_TEMPORARILY)) {
            	if(maxRedirections > 0) {
	                Header location = method.getResponseHeader("Location");
	                if (!location.getValue().equals("")) {
	                    // recursively check URL until it's not redirected any more
	                    response = followRedirects(location.getValue(), maxRedirections - 1);
	                }
            	}
            }
        } catch (IOException ioe) {
        }
        return response;
    }
}
