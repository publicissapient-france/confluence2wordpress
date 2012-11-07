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
package fr.dutra.confluence2wordpress.core.converter.processors;

import java.util.List;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import fr.dutra.confluence2wordpress.core.converter.ConverterOptions;
import fr.dutra.confluence2wordpress.core.metadata.MetadataManager;


public class IgnoredMacrosPreProcessor extends MacroPreprocessor {

    public IgnoredMacrosPreProcessor(XhtmlContent xhtmlUtils, ConversionContext conversionContext) {
        super(xhtmlUtils, conversionContext);
    }

    @Override
    protected boolean shouldProcessMacro(ConverterOptions options, MacroDefinition macroDefinition) {
        String name = macroDefinition.getName();
        List<String> ignoredConfluenceMacros = options.getIgnoredConfluenceMacros();
        return 
            (ignoredConfluenceMacros != null && ignoredConfluenceMacros.contains(name)) || 
            MetadataManager.WORDPRESS_SYNC_INFO_MACRO_NAME.equals(name) || 
            MetadataManager.WORDPRESS_METADATA_MACRO_NAME.equals(name);
    }

    @Override
    protected String processMacro(ConverterOptions options, MacroDefinition macroDefinition) {
        return "";
    }

}
