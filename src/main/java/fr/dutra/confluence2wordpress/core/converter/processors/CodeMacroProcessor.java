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
/**
 * 
 */
package fr.dutra.confluence2wordpress.core.converter.processors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import fr.dutra.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;


/**
 * @author Alexandre Dutra
 *
 */
public class CodeMacroProcessor extends MacroToMacroProcessor {

    private static final String CONFLUENCE_MACRO_NAME = "code";

	private static final String LANGUAGE = "language";
	private static final String XML = "xml";
	private static final String HTML_XML = "html/xml";
	private static final String JAVA = "java";

    private final SyntaxHighlighterPlugin syntaxHighlighterPlugin;
    
	public CodeMacroProcessor(XhtmlContent xhtmlUtils, ConversionContext conversionContext, SyntaxHighlighterPlugin syntaxHighlighterPlugin) {
        super(xhtmlUtils, conversionContext);
        this.syntaxHighlighterPlugin = syntaxHighlighterPlugin;
    }

    @Override
	protected String getConfluenceMacroName() {
		return CONFLUENCE_MACRO_NAME;
	}

	@Override
	protected String getWordpressMacroName(MacroDefinition macroDefinition) {
		return getCodeLanguage(macroDefinition.getParameters());
	}

	@Override
	protected Map<String, String> getWordpressMacroParameters(MacroDefinition macroDefinition) throws XhtmlException {
		Map<String, String> confluenceParameters = macroDefinition.getParameters();
		Map<String, String> wordpressParameters = new HashMap<String, String>();
		Map<String, String> substitutionMap = syntaxHighlighterPlugin.getSubstitutionMap();
        for (Entry<String, String> entry : confluenceParameters.entrySet()) {
            String confluenceKey = entry.getKey();
            if(substitutionMap.containsKey(confluenceKey)){
                String wordpressKey = substitutionMap.get(confluenceKey);
                String value = entry.getValue();
                wordpressParameters.put(wordpressKey, value);
            }
        }
		return wordpressParameters;
	}

	private String getCodeLanguage(Map<String, String> parameters) {
		String language = parameters.get(LANGUAGE);
		if(language == null) {
			language = JAVA;
		}
        //no equivalent in wordpress
        if(HTML_XML.equals(language)) {
        	language = XML;
        }
		return language;
	}
    
}
