/**
 * 
 */
package fr.dutra.xebia.confluence2wordpress.util.string;



/**
 * @author Alexandre Dutra
 *
 */
public class XmlEscapeUtils{

    public static String escapeText(String text) {
        return text.replace("<", "&lt;").replace("&", "&amp;");
    }

}
