package fr.dutra.confluence2wordpress.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


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
}
