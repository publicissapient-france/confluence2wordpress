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
    
    /*
     * examples:
     * /confluence/download/attachments/983042/image.png?version=1&amp;modificationDate=1327402370523 (image)
     * /confluence/download/thumbnails/983042/image.png (thumbnail)
     * http://localhost:1990/confluence/download/attachments/983042/image.png (image)
     * /confluence/download/attachments/983042/pom.xml?version=1&amp;modificationDate=1327402370710 (attachment)
     * /confluence/download/attachments/983042/armonia.png?version=1&amp;modificationDate=1327402370523 (image as attachment)
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

}
