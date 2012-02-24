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
package fr.xebia.confluence2wordpress.core.converter.preprocessors;

import java.util.List;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionReplacer;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import fr.xebia.confluence2wordpress.core.converter.ConversionException;
import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;
import fr.xebia.confluence2wordpress.core.metadata.MetadataManager;


public class IgnoredMacrosPreProcessor implements PreProcessor {

    private final XhtmlContent xhtmlUtils;
    
    private final ConversionContext conversionContext;
    
    public IgnoredMacrosPreProcessor(XhtmlContent xhtmlUtils, ConversionContext conversionContext) {
        super();
        this.xhtmlUtils = xhtmlUtils;
        this.conversionContext = conversionContext;
    }

    @Override
    public String preProcess(String storage, ConverterOptions options) throws ConversionException {
    	final List<String> ignoredConfluenceMacros = options.getIgnoredConfluenceMacros();
		if(ignoredConfluenceMacros != null) {
	    	try {
				storage = xhtmlUtils.replaceMacroDefinitionsWithString(storage, conversionContext, new MacroDefinitionReplacer() {
					@Override
					public String replace(MacroDefinition macroDefinition) throws XhtmlException {
						String name = macroDefinition.getName();
						if(ignoredConfluenceMacros.contains(name) || MetadataManager.WORDPRESS_METADATA_MACRO_NAME.equals(name)){
							return "";
						}
						return xhtmlUtils.convertMacroDefinitionToStorage(macroDefinition, conversionContext);
					}
				});
			} catch (XhtmlException e) {
				throw new ConversionException("Could not preprocess storage", e);
			}
    	
        }
    	return storage;
    }

}
