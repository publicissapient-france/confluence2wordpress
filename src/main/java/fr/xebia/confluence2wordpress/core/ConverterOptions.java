/**
 * Copyright 2011 Alexandre Dutra
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.xebia.confluence2wordpress.core;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import fr.xebia.confluence2wordpress.util.collections.MapUtils;

public class ConverterOptions {

    private boolean omitXmlDeclaration = true;

    private boolean useCdataForScriptAndStyle = false;

    private boolean omitComments = true;

    private boolean useEmptyElementTags = false;

    private boolean convertFontTagToSpan = true;

    private boolean convertCdata = false;

    private boolean convertScriptsToWordpressFormat = false;

    private String baseUrl = null;

    private Map<String, String> tagTransformations = null;

    private List<String> disableConfluenceMacros = null;

    private boolean addRDPHeader = false;

    public ConverterOptions() {
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

    public boolean isConvertCdata() {
        return convertCdata;
    }

    public void setConvertCdata(boolean convertCdata) {
        this.convertCdata = convertCdata;
    }

    public boolean isConvertScriptsToWordpressFormat() {
        return convertScriptsToWordpressFormat;
    }

    public void setConvertScriptsToWordpressFormat(boolean convertScriptsToWordpressFormat) {
        this.convertScriptsToWordpressFormat = convertScriptsToWordpressFormat;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = StringUtils.trimToNull(baseUrl);
    }

    public Map<String, String> getTagTransformations() {
        return tagTransformations;
    }

    public void setTagTransformations(Map<String, String> tagTransformations) {
        //no hashtables here, and no empty strings
        this.tagTransformations = MapUtils.trimValues(tagTransformations);
    }

    public List<String> getDisableConfluenceMacros() {
        return disableConfluenceMacros;
    }

    public void setDisableConfluenceMacros(List<String> ignoreConfluenceMacros) {
        this.disableConfluenceMacros = ignoreConfluenceMacros;
    }

    public boolean isAddRDPHeader() {
        return addRDPHeader;
    }

    public void setAddRDPHeader(boolean addRDPHeader) {
        this.addRDPHeader = addRDPHeader;
    }

}