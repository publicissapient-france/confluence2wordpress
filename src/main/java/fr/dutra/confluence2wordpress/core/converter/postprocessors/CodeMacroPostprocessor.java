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
package fr.dutra.confluence2wordpress.core.converter.postprocessors;

import org.apache.commons.lang.StringEscapeUtils;
import org.htmlcleaner.TagNode;

import fr.dutra.confluence2wordpress.core.converter.ConverterOptions;
import fr.dutra.confluence2wordpress.core.converter.preprocessors.CodeMacroPreprocessor;


/**
 * @author Alexandre Dutra
 *
 */
public class CodeMacroPostprocessor implements PostProcessor {

	@Override
	public String postProcess(String html, TagNode body, ConverterOptions options) {
		int start = html.indexOf(CodeMacroPreprocessor.PRE_START);
		while(start != -1) {
			int end = html.indexOf(CodeMacroPreprocessor.PRE_END, start + CodeMacroPreprocessor.PRE_START.length());
			html = 
					html.substring(0, start) + 
					postProcessCode(html, start, end) + 
					html.substring(end + CodeMacroPreprocessor.PRE_END.length());
			start = html.indexOf(CodeMacroPreprocessor.PRE_START, end + CodeMacroPreprocessor.PRE_END.length());
		}
		return html;
	}

	private String postProcessCode(String html, int start, int end) {
		String code = html.substring(start + CodeMacroPreprocessor.PRE_START.length(), end);
		//confluence renderer converts xml special chars into entities eg. " -> &quot; :(
		//code = HtmlUtils.unescapeHtml(code);
		code = StringEscapeUtils.unescapeXml(code);
		//this is due to CodeMacroPreprocessor
		return code;
	}

}
