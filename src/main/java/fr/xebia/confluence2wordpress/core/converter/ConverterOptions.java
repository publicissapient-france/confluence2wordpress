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
package fr.xebia.confluence2wordpress.core.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;

import fr.xebia.confluence2wordpress.util.MapUtils;

public class ConverterOptions {

    private boolean omitXmlDeclaration = true;

    private boolean useCdataForScriptAndStyle = false;

    private boolean omitComments = false;

    private boolean useEmptyElementTags = false;

    private boolean convertFontTagToSpan = true;

    private Map<String, String> tagTransformations = null;

    private List<String> ignoredConfluenceMacros = null;

    private boolean optimizeForRDP = false;

    private boolean includeTOC = false;

    private Map<String, String> attachmentsMap;

    private SyntaxHighlighterPlugin syntaxHighlighterPlugin = SyntaxHighlighterPlugin.SH_LEGACY;
    
    public ConverterOptions() {
        this.tagTransformations = MapUtils.split("tt=code", ",", "=");
    }

    public ConverterOptions(Map<Object,Object> properties) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (Entry<Object, Object> entry : properties.entrySet()) {
            String name = entry.getKey().toString();
            Object value = entry.getValue();
            PropertyUtils.setProperty(this, name, value);
        }
    }

    public boolean isOmitXmlDeclaration() {
        return omitXmlDeclaration;
    }

    public void setOmitXmlDeclaration(boolean omitXmlDeclaration) {
        this.omitXmlDeclaration = omitXmlDeclaration;
    }

    public boolean isUseCdataForScriptAndStyle() {
        return useCdataForScriptAndStyle;
    }

    public void setUseCdataForScriptAndStyle(boolean useCdataForScriptAndStyle) {
        this.useCdataForScriptAndStyle = useCdataForScriptAndStyle;
    }

    public boolean isOmitComments() {
        return omitComments;
    }

    public void setOmitComments(boolean omitComments) {
        this.omitComments = omitComments;
    }

    public boolean isUseEmptyElementTags() {
        return useEmptyElementTags;
    }

    public void setUseEmptyElementTags(boolean useEmptyElementTags) {
        this.useEmptyElementTags = useEmptyElementTags;
    }

    public boolean isConvertFontTagToSpan() {
        return convertFontTagToSpan;
    }

    public void setConvertFontTagToSpan(boolean convertFontTagToSpan) {
        this.convertFontTagToSpan = convertFontTagToSpan;
    }

    public Map<String, String> getTagTransformations() {
        return tagTransformations;
    }

    public void setTagTransformations(Map<String, String> tagTransformations) {
        //no hashtables here, and no empty strings
        this.tagTransformations = MapUtils.trimValues(tagTransformations);
    }

    public List<String> getIgnoredConfluenceMacros() {
        return ignoredConfluenceMacros;
    }

    public void setIgnoredConfluenceMacros(List<String> disabledConfluenceMacros) {
        this.ignoredConfluenceMacros = disabledConfluenceMacros;
    }

    public boolean isOptimizeForRDP() {
        return optimizeForRDP;
    }

    public void setOptimizeForRDP(boolean optimizeForRDP) {
        this.optimizeForRDP = optimizeForRDP;
    }
    
    public boolean isIncludeTOC() {
        return includeTOC;
    }
    
    public void setIncludeTOC(boolean includeTOC) {
        this.includeTOC = includeTOC;
    }

    public Map<String, String> getAttachmentsMap() {
        return attachmentsMap;
    }

    public void setAttachmentsMap(Map<String, String> attachmentsMap) {
        this.attachmentsMap = attachmentsMap;
    }
    
    public SyntaxHighlighterPlugin getSyntaxHighlighterPlugin() {
        return syntaxHighlighterPlugin;
    }
    
    public void setSyntaxHighlighterPlugin(SyntaxHighlighterPlugin syntaxHighlighterPlugin) {
        this.syntaxHighlighterPlugin = syntaxHighlighterPlugin;
    }

}