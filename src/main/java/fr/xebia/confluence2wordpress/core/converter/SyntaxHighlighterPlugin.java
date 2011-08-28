package fr.xebia.confluence2wordpress.core.converter;

import java.util.HashMap;
import java.util.Map;


/**
 * @see "http://alexgorbatchev.com/SyntaxHighlighter/manual/configuration/"
 * @see "https://studio.plugins.atlassian.com/wiki/display/NCODE/Confluence+New+Code+Macro"
 *
 */
public enum SyntaxHighlighterPlugin {

    /**
     * @see "http://wordpress.org/extend/plugins/syntax-highlighter/"
     * @see "http://wppluginsj.sourceforge.jp/syntax-highlighter/"
     */
    SH_LEGACY {

        @Override
        public Map<String, String> getSubstitutionMap() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("first-line", "num");
            map.put("gutter", "gutter");
            return map;
        }
    },
    
    /**
     * @see "http://wordpress.org/extend/plugins/syntaxhighlighter/"
     * @see "http://en.support.wordpress.com/code/posting-source-code/"
     */
    SH_EVOLVED {

        @Override
        public Map<String, String> getSubstitutionMap() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("first-line", "firstline");
            map.put("collapse", "collapse");
            map.put("gutter", "gutter");
            return map;
        }
    }
    
    ;

    public abstract Map<String, String> getSubstitutionMap();
}
