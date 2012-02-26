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

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;
import fr.xebia.confluence2wordpress.core.converter.SyntaxHighlighterPlugin;
import fr.xebia.confluence2wordpress.util.HtmlUtils;


/**
 * @author Alexandre Dutra
 *
 */
public class CodeMacroPreprocessor extends PreProcessorBase {

    private static final String CODE_MACRO_NAME = "code";
    
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
        <ac:macro ac:name="code">
        <ac:parameter ac:name="title">mon code</ac:parameter>
        <ac:parameter ac:name="linenumbers">true</ac:parameter>
        <ac:parameter ac:name="language">java</ac:parameter>
        <ac:parameter ac:name="firstline">20</ac:parameter>
        <ac:parameter ac:name="collapse">true</ac:parameter>
        <ac:plain-text-body><![CDATA[...]]></ac:plain-text-body>
        </ac:macro>
            
        Converts to

        [java gutter=true firstline=20 collapse=true]
        ...
        [/java]

         */
        Map<String, String> parameters = macroDefinition.getParameters();
        StringBuilder sb = new StringBuilder();
        sb.append("\n[");
        String pluginTagName = syntaxHighlighterPlugin.getTagName(parameters.get("language"));
        sb.append(pluginTagName);
        Map<String, String> substitutionMap = syntaxHighlighterPlugin.getSubstitutionMap();
        for (Entry<String, String> entry : parameters.entrySet()) {
            String confluenceKey = entry.getKey();
            if(substitutionMap.containsKey(confluenceKey)){
                String wordpressKey = substitutionMap.get(confluenceKey);
                sb.append(' ').append(wordpressKey).append('=').append(entry.getValue());
            }
        }
        String code = HtmlUtils.stripCdata(macroDefinition.getBodyText());
        sb.append("]\n").
            append(code).
            append("\n[/").
            append(pluginTagName).
            append("]\n");
        return sb.toString();
    }

    
}
