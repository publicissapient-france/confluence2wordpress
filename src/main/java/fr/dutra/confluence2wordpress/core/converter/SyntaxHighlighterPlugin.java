/**
 * Copyright 2011 Alexandre Dutra
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
package fr.dutra.confluence2wordpress.core.converter;

import java.util.HashMap;
import java.util.Map;


/**
 * @see "http://alexgorbatchev.com/SyntaxHighlighter/manual/configuration/"
 * @see "https://studio.plugins.atlassian.com/wiki/display/NCODE/Confluence+New+Code+Macro"
 *
 */
public enum SyntaxHighlighterPlugin {

    /**
     * @see "http://wppluginsj.sourceforge.jp/syntax-highlighter/"
     */
    SH_LEGACY {

        @Override
        public Map<String, String> getSubstitutionMap() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("firstline", "num");
            map.put("linenumbers", "gutter");
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
            //map.put("language", "language");
            map.put("firstline", "firstline");
            map.put("collapse", "collapse");
            map.put("linenumbers", "gutter");
            return map;
        }

    }
    
    ;

    public abstract Map<String, String> getSubstitutionMap();

}
