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
package fr.xebia.confluence2wordpress.core.converter.preprocessors;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringEscapeUtils;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;
import fr.xebia.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;


/**
 * @author Alexandre Dutra
 *
 */
public class CodeMacroPreprocessor extends PreProcessorBase {

	/*
	 * We use "pre" tags so that code formatters do not mess up the code inside;
	 * the "lang" attribute is not wiped out by attribute cleaners.
	 */
	public static final String PRE_START = "<pre lang=\"c2w-syntaxhighlighter\">";
	public static final String PRE_END = "</pre>";

    private static final String CODE_MACRO_NAME = "code";

	private static final String LANGUAGE = "language";
	private static final String XML = "xml";
	private static final String HTML_XML = "html/xml";

	private static final String OPEN_BRACKET = "[";
	private static final String CLOSE_BRACKET = "]";
	private static final String OPEN_BRACKET_SLASH = "[/";

	private static final char EQUALS = '=';
	private static final char SPACE = ' ';
	private static final String LINE_BREAK = "\n";

    private final SyntaxHighlighterPlugin syntaxHighlighterPlugin;
    
    public CodeMacroPreprocessor(XhtmlContent xhtmlUtils, ConversionContext conversionContext, SyntaxHighlighterPlugin syntaxHighlighterPlugin) {
        super(xhtmlUtils, conversionContext);
        this.syntaxHighlighterPlugin = syntaxHighlighterPlugin;
    }

    @Override
    protected boolean shouldProcessMacro(ConverterOptions options, MacroDefinition macroDefinition) {
        return CODE_MACRO_NAME.equals(macroDefinition.getName());
    }

    @Override
    protected String processMacro(ConverterOptions options, MacroDefinition macroDefinition) {
    	
        /*
          
         Original storage:
         
        <ac:macro ac:name="code">
        <ac:parameter ac:name="title">my code</ac:parameter>
        <ac:parameter ac:name="linenumbers">true</ac:parameter>
        <ac:parameter ac:name="language">java</ac:parameter>
        <ac:parameter ac:name="firstline">20</ac:parameter>
        <ac:parameter ac:name="collapse">true</ac:parameter>
        <ac:plain-text-body><![CDATA[...]]></ac:plain-text-body>
        </ac:macro>
            
         */
        Map<String, String> parameters = macroDefinition.getParameters();
        StringBuilder sb = new StringBuilder();
        sb.append(PRE_START);
        appendSHPluginStartTag(sb, parameters);
        sb.append(LINE_BREAK);
        //we need to escape "<" and "&" at least, but we escape all XML entities since Confluence renderer 
        //would do it anyway
	    sb.append(StringEscapeUtils.escapeXml(macroDefinition.getBodyText()));
	    sb.append(LINE_BREAK);
	    appendSHPluginEndTag(sb, parameters);
        sb.append(PRE_END);
        String string = sb.toString();
		return string;
    }

	private void appendSHPluginStartTag(StringBuilder sb, Map<String, String> parameters) {
		sb.append(OPEN_BRACKET);
        String language = getCodeLanguage(parameters);
		sb.append(language);
        Map<String, String> substitutionMap = syntaxHighlighterPlugin.getSubstitutionMap();
        for (Entry<String, String> entry : parameters.entrySet()) {
            String confluenceKey = entry.getKey();
            if(substitutionMap.containsKey(confluenceKey)){
                String wordpressKey = substitutionMap.get(confluenceKey);
                String value = entry.getValue();
				sb.append(SPACE).append(wordpressKey).append(EQUALS).append(value);
            }
        }
        sb.append(CLOSE_BRACKET);
	}

	private void appendSHPluginEndTag(StringBuilder sb, Map<String, String> parameters) {
		sb.append(OPEN_BRACKET_SLASH);
        String language = getCodeLanguage(parameters);
        sb.append(language);
        sb.append(CLOSE_BRACKET);
	}

	private String getCodeLanguage(Map<String, String> parameters) {
		String language = parameters.get(LANGUAGE);
        //no equivalent in wordpress
        if(HTML_XML.equals(language)) {
        	language = XML;
        }
		return language;
	}
    
}
