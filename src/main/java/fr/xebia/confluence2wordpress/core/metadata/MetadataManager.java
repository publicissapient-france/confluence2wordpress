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
package fr.xebia.confluence2wordpress.core.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.core.ContentEntityObject;

import fr.xebia.confluence2wordpress.wp.WordpressCategory;
import fr.xebia.confluence2wordpress.wp.WordpressUser;

/**
 * @author Alexandre Dutra
 *
 */
public class MetadataManager {

    private static final String WARNING = "## PLEASE DO NOT EDIT THIS SECTION MANUALLY!";

    private static final String LINE_SEPARATOR = "\r\n";

    private static final String WORDPRESS_META_START = "{details:label=WordpressMetadata|hidden=true}";

    private static final String WORDPRESS_META_END = "{details}";

    private static final Pattern DRAFT_PREFIX_PATTERN = Pattern.compile("(DRAFT\\s*-\\s*).+");

    private MetadataSerializer serializer = new MetadataSerializer();

    public Metadata extractMetadata(ContentEntityObject page) throws MetadataException {
        String content = page.getContent();
        int start = content.indexOf(WORDPRESS_META_START);
        if(start != -1){
            start += WORDPRESS_META_START.length();
            int end = content.indexOf(WORDPRESS_META_END, start);
            if(end != -1){
                String macroBody = content.substring(start, end);
                Metadata metadata = parseMacroBody(macroBody);
                return metadata;
            }
        }
        return null;
    }

    public void storeMetadata(ContentEntityObject page, Metadata metadata) throws MetadataException{
        String content = page.getContent();
        int start = content.indexOf(WORDPRESS_META_START);
        if(start != -1){
            start += WORDPRESS_META_START.length();
            int end = content.indexOf(WORDPRESS_META_END, start);
            if(end != -1){
                StringBuilder macroBody = buildMacroBody(metadata, false);
                content = content.substring(0, start) + macroBody.toString() + content.substring(end);
                page.setContent(content);
                return;
            }
        }
        StringBuilder macroBody = buildMacroBody(metadata, true);
        content = content + macroBody.toString();
        page.setContent(content);
    }


    public Metadata createMetadata(
        ContentEntityObject page, List<WordpressUser> users, 
        Set<WordpressCategory> categories, List<String> ignoreConfluenceMacros) throws MetadataException {
        Metadata metadata = new Metadata();
        String pageTitle = page.getTitle();
        Matcher matcher = DRAFT_PREFIX_PATTERN.matcher(pageTitle);
        if(matcher.matches()){
            String prefix = matcher.group(1);
            metadata.setPageTitle(StringUtils.substringAfter(pageTitle, prefix));
        } else {
            metadata.setPageTitle(pageTitle);
        }
        if(StringUtils.containsIgnoreCase(metadata.getPageTitle(), "revue de presse")) {
            metadata.setOptimizeForRDP(true);
            metadata.setPageTitle("Revue de Presse Xebia");
            for (WordpressCategory category : categories) {
                if(StringUtils.containsIgnoreCase(category.getCategoryName(), "revue de presse")){
                    metadata.setCategoryNames(Collections.singletonList(category.getCategoryName()));
                    break;
                }
            }
            Calendar now = Calendar.getInstance();
            metadata.setPostSlug(String.format("revue-de-presse-xebia-%1$tY-%2$02d", now.getTime(), now.get(Calendar.WEEK_OF_YEAR)));
            for (WordpressUser user : users) {
                if("xebia-france".equals(user.getDisplayName())){
                    metadata.setAuthorId(user.getId());
                    break;
                }
            }
        } else {
            String creatorName = page.getCreatorName();
            if(creatorName != null) {
                for (WordpressUser wordpressUser : users) {
                    if(creatorName.equals(wordpressUser.getLogin())){
                        metadata.setAuthorId(wordpressUser.getId());
                        break;
                    }
                }
            }
        }
        metadata.setIgnoreConfluenceMacros(ignoreConfluenceMacros);
        return metadata;
    }

 
    private Map<String,String> describe(Metadata metadata) throws MetadataException {
        Map<String,String> properties = new HashMap<String,String>();
        Field[] declaredFields = metadata.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            MetadataItem annotation = field.getAnnotation(MetadataItem.class);
            if(annotation != null){
                String key = annotation.value();
                field.setAccessible(true);
                Object rawValue;
                try {
                    rawValue = field.get(metadata);
                } catch (IllegalArgumentException e) {
                    throw new MetadataException("Cannot access field value: " + field.getName(), e);
                } catch (IllegalAccessException e) {
                    throw new MetadataException("Cannot access field value: " + field.getName(), e);
                }
                String value = serializer.serialize(rawValue);
                properties.put(key, value);
            }
        }
        return properties;
    }

    private Metadata copyProperties(Metadata metadata, Map<String,String> properties) throws MetadataException{
        Field[] declaredFields = metadata.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            MetadataItem annotation = field.getAnnotation(MetadataItem.class);
            if(annotation != null){
                String key = annotation.value();
                field.setAccessible(true);
                if(properties.containsKey(key)){
                    String serialized = properties.get(key);
                    Class<?> fieldType = field.getType();
                    Class<?> elementType = null;
                    Object value;
                    if(List.class.isAssignableFrom(fieldType) && field.getGenericType() instanceof ParameterizedType){
                        elementType = (Class<?>) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                    }
                    value = serializer.deserialize(serialized, fieldType, elementType);
                    try {
                        field.set(metadata, value);
                    } catch (IllegalArgumentException e) {
                        throw new MetadataException("Cannot access field value: " + field.getName(), e);
                    } catch (IllegalAccessException e) {
                        throw new MetadataException("Cannot access field value: " + field.getName(), e);
                    }
                }
            }
        }
        return metadata;
    }

    private Metadata parseMacroBody(String macroBody) throws MetadataException {
        BufferedReader br = new BufferedReader(new StringReader(macroBody));
        Map<String, String> properties = new HashMap<String, String>();
        String line = null;
        try {
            while((line = br.readLine()) != null){
                int colon = line.indexOf(":");
                if(colon != -1){
                    String key = StringUtils.trimToNull(line.substring(0, colon));
                    String value = line.length() == colon + 1 ? null : StringUtils.trimToNull(line.substring(colon + 1));
                    properties.put(key, value);
                }
            }
        } catch (IOException e) {
        }
        Metadata metadata = new Metadata();
        return copyProperties(metadata, properties);
    }

    private StringBuilder buildMacroBody(Metadata metadata, boolean includeMacroDelimiter) throws MetadataException {
        Map<String,String> properties = describe(metadata);
        StringBuilder sb = new StringBuilder();
        if(includeMacroDelimiter) {
            sb.append(LINE_SEPARATOR);
            sb.append(WORDPRESS_META_START);
        }
        sb.append(LINE_SEPARATOR);
        sb.append(WARNING);
        sb.append(LINE_SEPARATOR);
        //can't use store here
        //properties.store(sw, null);
        for (Entry<String,String> entry : properties.entrySet()) {
            if(entry.getValue() != null){
                sb.append("    ");
                sb.append(entry.getKey());
                sb.append(" : ");
                sb.append(entry.getValue());
                sb.append(LINE_SEPARATOR);
            }
        } 
        if(includeMacroDelimiter) {
            sb.append(WORDPRESS_META_END);
            sb.append(LINE_SEPARATOR);
        }
        return sb;
    }
 
}