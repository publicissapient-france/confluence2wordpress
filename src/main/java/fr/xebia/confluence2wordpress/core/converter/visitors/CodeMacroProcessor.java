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
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import fr.xebia.confluence2wordpress.core.converter.preprocessors.CodeMacroPreprocessor;
import fr.xebia.confluence2wordpress.util.HtmlUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class CodeMacroProcessor implements TagNodeVisitor {

    public boolean visit(TagNode parentNode, HtmlNode htmlNode) {
        if (htmlNode instanceof TagNode) {
            TagNode script = (TagNode) htmlNode;
            String tagName = script.getName();
            if ("script".equals(tagName) && 
            		script.getAttributeByName("type") != null && 
            		script.getAttributeByName("type").equals(CodeMacroPreprocessor.SCRIPT_TYPE)) {
            	String content = script.getText().toString();
            	String stripCdata = HtmlUtils.stripCdata(content);
                //need to unescape because even between cdata characters like "<" come escaped
                String replacement = StringEscapeUtils.unescapeHtml(StringUtils.trim(stripCdata));
                ContentNode code = new ContentNode(replacement);
				parentNode.replaceChild(script, code);
				parentNode.insertChildAfter(code, new ContentNode("\n\n"));
            }
        }
        return true;
    }


}
