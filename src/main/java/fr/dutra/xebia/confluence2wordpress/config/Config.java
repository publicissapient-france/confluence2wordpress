/**
 * 
 */
package fr.dutra.xebia.confluence2wordpress.config;

import java.util.Calendar;



/**
 * @author Alexandre Dutra
 *
 */
public class Config {

    private static final String DEFAULT_RDP_URL = "http://blog.xebia.fr/%1$tY/%1$tm/%1$td/revue-de-presse-xebia/";

    private static final String DEFAULT_UPLOADED_FILES_BASE_URL = "http://blog.xebia.fr/wp-content/uploads/%1$tY/%1$tm/";

    public static String defaultUploadedFilesBaseUrl() {
        return String.format(DEFAULT_UPLOADED_FILES_BASE_URL, Calendar.getInstance());
    }

    public static String defaultRevueDePresseUrl() {
        return String.format(DEFAULT_RDP_URL, Calendar.getInstance());
    }

}
