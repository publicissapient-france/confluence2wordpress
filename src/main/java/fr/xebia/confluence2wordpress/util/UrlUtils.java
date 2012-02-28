package fr.xebia.confluence2wordpress.util;

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

}
