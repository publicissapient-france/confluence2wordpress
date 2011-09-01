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
/**
 * 
 */
package fr.xebia.confluence2wordpress.core.converter.visitors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlcleaner.TagNode;

import fr.xebia.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;


/**
 * @author Alexandre Dutra
 *
 */
public class LegacyCodeMacroProcessor extends NewCodeMacroProcessor {


    public LegacyCodeMacroProcessor(SyntaxHighlighterPlugin plugin) {
		super(plugin);
	}
    
    protected String findCode(TagNode divCodeContent) {
        TagNode pre = divCodeContent.findElementByName("pre", false);
        if(pre != null) {
            String code = pre.getText().toString();
            if(code.startsWith("<![CDATA[") && code.endsWith("]]>")) {
                code = StringUtils.substringBetween(code, "<![CDATA[", "]]>");
            }
            return StringEscapeUtils.unescapeHtml(StringUtils.trim(code));
        }
        return null;
    }

}
