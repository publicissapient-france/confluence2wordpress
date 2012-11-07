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
package fr.dutra.confluence2wordpress.core.converter.processors;

import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringEscapeUtils;
import org.htmlcleaner.TagNode;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.Maps;

import fr.dutra.confluence2wordpress.core.converter.ConverterOptions;


/**
 * @author Alexandre Dutra
 *
 */
public abstract class MacroToMacroProcessor extends MacroPreprocessor implements PreProcessor, PostProcessor {

	/*
	 * We use "pre" tags so that code formatters do not mess up the code inside;
	 * the "lang" attribute is not wiped out by attribute cleaners.
	 */
	protected static final String PRE_START = "<pre lang=\"c2w-";
	protected static final String PRE_START2 = "\">";
	protected static final String PRE_END = "</pre>";

	protected static final String OPEN_BRACKET = "[";
	protected static final String CLOSE_BRACKET = "]";
	protected static final String OPEN_BRACKET_SLASH = "[/";

	protected static final String QUOTE = "\"";
	protected static final String EQUALS = "=";
	protected static final String SPACE = " ";
	protected static final String LINE_BREAK = "\n";
	
	private static final Function<String, String> DOUBLE_ESCAPE_XML_THEN_QUOTE = new Function<String, String>() {
		
		@Override
		public String apply(@Nullable String input) {
			return QUOTE + doubleEscape(input) + QUOTE;
		}

		/**
		 * We need to double-escape strings because Confluence renderer also escapes
		 * XML characters, and the we unescape one level at the post-processing phase.
		 * @param s
		 * @return
		 */
		protected String doubleEscape(String s) {
			return StringEscapeUtils.escapeXml(StringEscapeUtils.escapeXml(s));
		}
		
	};

	private static final MapJoiner JOINER = Joiner.on(SPACE).withKeyValueSeparator(EQUALS);
	
    public MacroToMacroProcessor(XhtmlContent xhtmlUtils, ConversionContext conversionContext) {
        super(xhtmlUtils, conversionContext);
    }

    protected abstract String getConfluenceMacroName();
    
    protected abstract String getWordpressMacroName(MacroDefinition macroDefinition) throws XhtmlException;

    protected abstract Map<String,String> getWordpressMacroParameters(MacroDefinition macroDefinition) throws XhtmlException;

	protected String getWordpressMacroBody(MacroDefinition macroDefinition) throws XhtmlException {
		return StringEscapeUtils.escapeXml(macroDefinition.getBodyText());
	}

    @Override
    protected boolean shouldProcessMacro(ConverterOptions options, MacroDefinition macroDefinition) {
        return getConfluenceMacroName().equals(macroDefinition.getName());
    }

    @Override
    protected String processMacro(ConverterOptions options, MacroDefinition macroDefinition) throws XhtmlException {
        StringBuilder sb = new StringBuilder();
        sb.append(getStartTag());
        sb.append(getWordpressMacroStartTag(macroDefinition));
        sb.append(getWordpressMacroBody(macroDefinition));
        sb.append(getWordpressMacroEndTag(macroDefinition));
        sb.append(getEndTag());
        String string = sb.toString();
		return string;
    }

	@Override
	public String postProcess(String html, TagNode body, ConverterOptions options) {
		String startTag = getStartTag();
		String endTag = getEndTag();
		int start = html.indexOf(startTag);
		while(start != -1) {
			int end = html.indexOf(endTag, start + startTag.length());
			html = 
				html.substring(0, start) + 
				postProcessCode(html, start, end) + 
				html.substring(end + endTag.length());
			start = html.indexOf(startTag, end + endTag.length());
		}
		return html;
	}
	
	protected String getStartTag() {
		return PRE_START + getConfluenceMacroName() + PRE_START2;
	}

	protected String getEndTag() {
		return PRE_END;
	}

	protected String getWordpressMacroStartTag(MacroDefinition macroDefinition) throws XhtmlException {
		return OPEN_BRACKET + getWordpressMacroName(macroDefinition) + SPACE + getWordpressMacroParametersAsString(macroDefinition) + CLOSE_BRACKET;
	}

	protected String getWordpressMacroEndTag(MacroDefinition macroDefinition) throws XhtmlException {
		return OPEN_BRACKET_SLASH + getWordpressMacroName(macroDefinition) + CLOSE_BRACKET;
	}

	protected String getWordpressMacroParametersAsString(MacroDefinition macroDefinition) throws XhtmlException {
		Map<String, String> map = getWordpressMacroParameters(macroDefinition);
		return JOINER.join(Maps.transformValues(map, DOUBLE_ESCAPE_XML_THEN_QUOTE));
	}

	protected String postProcessCode(String html, int start, int end) {
		String code = html.substring(start + getStartTag().length(), end);
		//confluence renderer converts xml special chars into entities eg. " -> &quot; :(
		code = StringEscapeUtils.unescapeXml(code);
		return code;
	}


}
