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
package fr.dutra.confluence2wordpress.core.converter.preprocessors;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionReplacer;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import fr.dutra.confluence2wordpress.core.converter.ConversionException;
import fr.dutra.confluence2wordpress.core.converter.ConverterOptions;


public abstract class PreProcessorBase implements PreProcessor {

    private final XhtmlContent xhtmlUtils;
    
    private final ConversionContext conversionContext;
    
    public PreProcessorBase(XhtmlContent xhtmlUtils, ConversionContext conversionContext) {
        super();
        this.xhtmlUtils = xhtmlUtils;
        this.conversionContext = conversionContext;
    }

    @Override
    public String preProcess(String storage, final ConverterOptions options) throws ConversionException {
        try {
            storage = xhtmlUtils.replaceMacroDefinitionsWithString(storage, conversionContext, new MacroDefinitionReplacer() {
                @Override
                public String replace(MacroDefinition macroDefinition) throws XhtmlException {
                    if(shouldProcessMacro(options, macroDefinition)){
                        return processMacro(options, macroDefinition);
                    }
                    return xhtmlUtils.convertMacroDefinitionToStorage(macroDefinition, conversionContext);
                }

            });
        } catch (XhtmlException e) {
            throw new ConversionException("Could not preprocess storage: " + e.getMessage(), e);
        }
    	return storage;
    }

    protected abstract boolean shouldProcessMacro(ConverterOptions options, MacroDefinition macroDefinition);

    protected abstract String processMacro(ConverterOptions options, MacroDefinition macroDefinition);


}

