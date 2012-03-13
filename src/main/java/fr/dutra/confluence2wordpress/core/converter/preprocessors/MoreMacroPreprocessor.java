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
package fr.dutra.confluence2wordpress.core.converter.preprocessors;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import fr.dutra.confluence2wordpress.core.converter.ConverterOptions;


/**
 * @author Alexandre Dutra
 *
 */
public class MoreMacroPreprocessor extends PreProcessorBase {

	public static final String WORDPRESS_MORE = "more";
	
	private static final String WORDPRESS_MORE_TAG = "<" + WORDPRESS_MORE + "/>";

	public MoreMacroPreprocessor(XhtmlContent xhtmlUtils, ConversionContext conversionContext) {
		super(xhtmlUtils, conversionContext);
	}

	@Override
	protected boolean shouldProcessMacro(ConverterOptions options, MacroDefinition macroDefinition) {
		return macroDefinition.getName().equals(WORDPRESS_MORE);
	}

	@Override
	protected String processMacro(ConverterOptions options, MacroDefinition macroDefinition) {
		return WORDPRESS_MORE_TAG;
	}

}
