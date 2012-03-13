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
package fr.dutra.confluence2wordpress.core.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

import fr.dutra.confluence2wordpress.wp.WordpressCategory;
import fr.dutra.confluence2wordpress.wp.WordpressUser;

/**
 * @author Alexandre Dutra
 *
 */
public class MetadataManager {

	private static final String SPACE = " ";

	private static final String INDENTATION = "    ";

    private static final String WARNING = "## PLEASE DO NOT EDIT THIS SECTION MANUALLY!";

	private static final String LINE_SEPARATOR = "\r\n";

	private static final String WORDPRESS_META_START = "{details:label=WordpressMetadata|hidden=true}";

    private static final String WORDPRESS_META_END = "{details}";

    private static final String WORDPRESS_SYNC_INFO = "{wordpress-sync-info}";

    private static final Pattern DRAFT_PREFIX_PATTERN = Pattern.compile("(DRAFT\\s*-\\s*).+");

	private static final String KEY_VALUE_SEPARATOR = ":";

	private static final String RDP_CATEGORY_NAME = "Revue de presse";

	private static final String RDP_PAGE_TITLE = "Revue de Presse Xebia";

	private static final String RDP_POST_SLUG_FORMAT = "revue-de-presse-xebia-%1$tY-%2$02d";

	private static final String XEBIA_FRANCE_LOGIN = "XebiaFrance";

    private MetadataSerializer serializer = new MetadataSerializer();

    public Metadata extractMetadata(ContentEntityObject page) throws MetadataException {
        String content = page.getContent();
        int start = content.indexOf(WORDPRESS_META_START);
        if(start != -1){
            start += WORDPRESS_META_START.length();
            int end = content.indexOf(WORDPRESS_META_END, start);
            if(end != -1){
                String macroBody = content.substring(start, end);
                Map<String, String> macroParameters = readMetadataMacroBody(macroBody);
				Metadata metadata = createMetadata(macroParameters);
                return metadata;
            }
        }
        return null;
    }

    public void storeMetadata(ContentEntityObject page, Metadata metadata) throws MetadataException{
        String content = page.getContent();
    	Map<String, String> macroParameters = getMacroParameters(metadata);
        StringBuilder newContent = new StringBuilder();
        if(metadata.getPostId() != null && content.indexOf(WORDPRESS_SYNC_INFO) == -1) {
			newContent.append(WORDPRESS_SYNC_INFO);
			newContent.append(LINE_SEPARATOR);
			newContent.append(LINE_SEPARATOR);
        }
        StringBuilder metadataMacroBody = writeMetadataMacroBody(macroParameters);
        int start = content.indexOf(WORDPRESS_META_START);
        int end = start == -1 ? -1 : content.indexOf(WORDPRESS_META_END, start);
        if(start != -1 && end != -1){
        	newContent.append(content.substring(0, start));
        	newContent.append(metadataMacroBody);
        	newContent.append(content.substring(end + WORDPRESS_META_END.length()));
        } else {
        	newContent.append(content);
        	newContent.append(LINE_SEPARATOR);
        	newContent.append(LINE_SEPARATOR);
        	newContent.append(metadataMacroBody);
        	newContent.append(LINE_SEPARATOR);
        }
        page.setContent(newContent.toString());
    }

    public Metadata createMetadata(
        ContentEntityObject page, 
        Set<WordpressUser> users, 
        Set<WordpressCategory> categories) {
        Metadata metadata = new Metadata();
        String pageTitle = page.getTitle();
        Matcher matcher = DRAFT_PREFIX_PATTERN.matcher(pageTitle);
        if(matcher.matches()){
            String prefix = matcher.group(1);
            metadata.setPageTitle(StringUtils.substringAfter(pageTitle, prefix));
        } else {
            metadata.setPageTitle(pageTitle);
        }
        if(StringUtils.containsIgnoreCase(metadata.getPageTitle(), RDP_CATEGORY_NAME)) {
            metadata.setOptimizeForRDP(true);
            metadata.setPageTitle(RDP_PAGE_TITLE); // to normalize the title
            if(categories != null) {
	            for (WordpressCategory category : categories) {
	                if(StringUtils.containsIgnoreCase(category.getCategoryName(), RDP_CATEGORY_NAME)){
	                    metadata.setCategoryNames(Collections.singletonList(category.getCategoryName()));
	                    break;
	                }
	            }
            }
            Calendar now = Calendar.getInstance();
            metadata.setPostSlug(String.format(RDP_POST_SLUG_FORMAT, now.getTime(), now.get(Calendar.WEEK_OF_YEAR)));
            if(users != null) {
            	for (WordpressUser user : users) {
	                if(XEBIA_FRANCE_LOGIN.equals(user.getLogin())){
	                    metadata.setAuthorId(user.getId());
	                    break;
	                }
	            }
            }
        } else {
            String creatorName = page.getCreatorName();
            if(creatorName != null && users != null) {
                for (WordpressUser wordpressUser : users) {
                    if(creatorName.equals(wordpressUser.getLogin())){
                        metadata.setAuthorId(wordpressUser.getId());
                        break;
                    }
                }
            }
        }
        return metadata;
    }


    public Metadata createMetadata(Map<String,String> macroParameters) throws MetadataException{
    	Metadata metadata = new Metadata();
    	Field[] declaredFields = metadata.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            MetadataItem annotation = field.getAnnotation(MetadataItem.class);
            if(annotation != null){
                String key = annotation.value();
                field.setAccessible(true);
                if(macroParameters.containsKey(key)){
                    String serialized = macroParameters.get(key);
                    Class<?> fieldType = field.getType();
                    Object value;
					if(List.class.isAssignableFrom(fieldType) && field.getGenericType() instanceof ParameterizedType){
	                    Type[] typeArgs = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
                    	Class<?> elementType = (Class<?>) typeArgs[0];
                        value = serializer.deserializeList(serialized, elementType);
                    } else if(Map.class.isAssignableFrom(fieldType) && field.getGenericType() instanceof ParameterizedType){
                        Type[] typeArgs = ((ParameterizedType)field.getGenericType()).getActualTypeArguments();
                    	Class<?> keyType = (Class<?>) typeArgs[0];
                    	Class<?> valueType = (Class<?>) typeArgs[1];
                        value = serializer.deserializeMap(serialized, keyType, valueType);
                    } else {
                        value = serializer.deserialize(serialized, fieldType);
                    }
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

    public Map<String,String> getMacroParameters(Metadata metadata) throws MetadataException {
        Map<String,String> macroParameters = new HashMap<String,String>();
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
                Class<?> fieldType = field.getType();
                String value;
                if(List.class.isAssignableFrom(fieldType)){
                	value = serializer.serializeList((List<?>) rawValue);
                } else if(Map.class.isAssignableFrom(fieldType)){
                	value = serializer.serializeMap((Map<?, ?>) rawValue);
                } else {
                	value = serializer.serialize(rawValue);
                }
                macroParameters.put(key, value);
            }
        }
        return macroParameters;
    }
    
    public Map<String, String> readMetadataMacroBody(String macroBody) {
        Map<String, String> macroParameters = new HashMap<String, String>();
        BufferedReader br = new BufferedReader(new StringReader(macroBody));
        String line = null;
        try {
            while((line = br.readLine()) != null){
                int colon = line.indexOf(KEY_VALUE_SEPARATOR);
                if(colon != -1){
                    String key = StringUtils.trimToNull(line.substring(0, colon));
                    String value = line.length() == colon + 1 ? null : StringUtils.trimToNull(line.substring(colon + 1));
                    macroParameters.put(key, value);
                }
            }
        } catch (IOException e) {
        }
        return macroParameters;
    }

    public StringBuilder writeMetadataMacroBody(Map<String,String> macroParameters) {
        StringBuilder sb = new StringBuilder();
        sb.append(WORDPRESS_META_START);
        sb.append(LINE_SEPARATOR);
        sb.append(INDENTATION);
        sb.append(WARNING);
        sb.append(LINE_SEPARATOR);
        //can't use store here
        //properties.store(sw, null);
        for (Entry<String,String> entry : macroParameters.entrySet()) {
            if(entry.getValue() != null){
                sb.append(INDENTATION);
                sb.append(entry.getKey());
                sb.append(SPACE);
                sb.append(KEY_VALUE_SEPARATOR);
                sb.append(SPACE);
                sb.append(entry.getValue());
                sb.append(LINE_SEPARATOR);
            }
        } 
        sb.append(WORDPRESS_META_END);
        return sb;
    }


}