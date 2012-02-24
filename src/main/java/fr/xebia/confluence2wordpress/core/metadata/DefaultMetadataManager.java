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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import fr.xebia.confluence2wordpress.wp.WordpressCategory;
import fr.xebia.confluence2wordpress.wp.WordpressUser;

/**
 * @author Alexandre Dutra
 *
 */
public class DefaultMetadataManager implements MetadataManager {
	
	//see com.atlassian.confluence.content.render.xhtml.XhtmlConstants
	// see com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants
    
	private static final String WORDPRESS_META_TAG_START = "<ac:macro ac:name=\"wordpress-metadata\">";

	private static final String WORDPRESS_META_PARAMETER = "<ac:parameter ac:name=\"%s\">%s</ac:parameter>";

    private static final String WORDPRESS_META_TAG_END = "</ac:macro>";

    private static final Pattern DRAFT_PREFIX_PATTERN = Pattern.compile("(DRAFT\\s*-\\s*).+");

	private static final String RDP_CATEGORY_NAME = "Revue de presse";

	private static final String RDP_PAGE_TITLE = "Revue de Presse Xebia";

	private static final String RDP_POST_SLUG_FORMAT = "revue-de-presse-xebia-%1$tY-%2$02d";

	private static final String XEBIA_FRANCE_LOGIN = "XebiaFrance";
	
    private final MetadataSerializer serializer = new MetadataSerializer();

	private final XhtmlContent xhtmlUtils;

    public DefaultMetadataManager(XhtmlContent xhtmlUtils) {
		super();
		this.xhtmlUtils = xhtmlUtils;
	}

    public Metadata extractMetadata(ContentEntityObject page) throws MetadataException {
    	//https://developer.atlassian.com/display/CONFDEV/Creating+a+new+Confluence+4.0+Macro
    	final List<MacroDefinition> metadataMacros = new ArrayList<MacroDefinition>();
		try {
			xhtmlUtils.handleMacroDefinitions(page.getBodyAsString(), null, new MacroDefinitionHandler() {
				@Override
				public void handle(MacroDefinition macroDefinition) {
					if(macroDefinition.getName().equals(WORDPRESS_METADATA_MACRO_NAME)){
						metadataMacros.add(macroDefinition);
					}
				}
			});
		} catch (XhtmlException e) {
			throw new MetadataException("Could not parse page: " + page.getTitle(), e);
		}
		if(metadataMacros.isEmpty()) {
			return null;
		}
		MacroDefinition metadataMacro = metadataMacros.get(0);
		return createMetadata(metadataMacro.getParameters());
    }
    
    public void storeMetadata(ContentEntityObject page, Metadata metadata) throws MetadataException{
        String content = page.getBodyAsString();
    	Map<String, String> macroParameters = getMacroParameters(metadata);
        StringBuilder newContent = new StringBuilder();
        StringBuilder metadataMacroBody = writeMetadataMacroBody(macroParameters);
        int start = content.indexOf(WORDPRESS_META_TAG_START);
        int end = start == -1 ? -1 : content.indexOf(WORDPRESS_META_TAG_END, start);
        if(start != -1 && end != -1){
        	newContent.append(content.substring(0, start));
        	newContent.append(metadataMacroBody);
        	newContent.append(content.substring(end + WORDPRESS_META_TAG_END.length()));
        } else {
        	newContent.append(metadataMacroBody);
        	newContent.append(content);
        }
        page.setBodyAsString(newContent.toString());
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
                String key = field.getName();
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

    private Map<String,String> getMacroParameters(Metadata metadata) throws MetadataException {
        Map<String,String> macroParameters = new HashMap<String,String>();
        Field[] declaredFields = metadata.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            MetadataItem annotation = field.getAnnotation(MetadataItem.class);
            if(annotation != null){
                String key = field.getName();
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
    
    private StringBuilder writeMetadataMacroBody(Map<String,String> macroParameters) {
        StringBuilder sb = new StringBuilder();
        sb.append(WORDPRESS_META_TAG_START);
        for (Entry<String,String> entry : macroParameters.entrySet()) {
            if(entry.getValue() != null){
                sb.append(String.format(WORDPRESS_META_PARAMETER, entry.getKey(), entry.getValue()));
            }
        } 
        sb.append(WORDPRESS_META_TAG_END);
        return sb;
    }
    


}