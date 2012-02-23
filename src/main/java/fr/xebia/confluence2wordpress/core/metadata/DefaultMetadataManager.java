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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.core.ContentEntityObject;
import com.google.common.base.Splitter;

import fr.xebia.confluence2wordpress.core.labels.PageLabelsSynchronizer;
import fr.xebia.confluence2wordpress.wp.WordpressCategory;
import fr.xebia.confluence2wordpress.wp.WordpressUser;

/**
 * @author Alexandre Dutra
 *
 */
public class DefaultMetadataManager implements MetadataManager {

	private static final String WORDPRESS_META_WRAP_START = "<div style=\"display: none;\"><p>&nbsp;</p>";

	private static final String WORDPRESS_META_TAG_START = "<ac:macro ac:name=\"details\">";

	private static final String WORDPRESS_META_BODY_START = "<ac:rich-text-body><p><strong>Please do not edit this section manually!</strong></p><table><tbody>";

    private static final String WORDPRESS_META_BODY_END = "</tbody></table></ac:rich-text-body>";

    private static final String WORDPRESS_META_TAG_END = "</ac:macro>";

    private static final String WORDPRESS_META_WRAP_END = "</div>";

    private static final String WORDPRESS_SYNC_INFO = "<ac:macro ac:name=\"wordpress-sync-info\" />";

	private static final String TABLE_LINE_START = "<tr><td>";

	private static final String TABLE_LINE_MIDDLE = "</td><td>";

	private static final String TABLE_LINE_END = "</td></tr>";

    private static final Pattern DRAFT_PREFIX_PATTERN = Pattern.compile("(DRAFT\\s*-\\s*).+");

	private static final String RDP_CATEGORY_NAME = "Revue de presse";

	private static final String RDP_PAGE_TITLE = "Revue de Presse Xebia";

	private static final String RDP_POST_SLUG_FORMAT = "revue-de-presse-xebia-%1$tY-%2$02d";

	private static final String XEBIA_FRANCE_LOGIN = "XebiaFrance";
	
	private static final Splitter ROW_SPLITTER = Splitter.onPattern("(</?tr>){1,2}");
	
	private static final Pattern ROW_PATTERN = Pattern.compile("<td>(.+)</td><td>(.+)</td>");

    private MetadataSerializer serializer = new MetadataSerializer();

    private PageLabelsSynchronizer pageLabelsSynchronizer;
    
    public DefaultMetadataManager(PageLabelsSynchronizer pageLabelsSynchronizer) {
		super();
		this.pageLabelsSynchronizer = pageLabelsSynchronizer;
	}

	/**
	 * @see fr.xebia.confluence2wordpress.core.metadata.MetadataManager#extractMetadata(com.atlassian.confluence.core.ContentEntityObject)
	 * {@inheritDoc}
	 */
    public Metadata extractMetadata(ContentEntityObject page) throws MetadataException {
        String content = page.getBodyAsString();
        int start = content.indexOf(WORDPRESS_META_TAG_START);
        if(start != -1){
            start += WORDPRESS_META_TAG_START.length();
            int end = content.indexOf(WORDPRESS_META_TAG_END, start);
            if(end != -1){
                String macroBody = content.substring(start, end);
                Map<String, String> macroParameters = readMetadataMacroBody(macroBody);
				Metadata metadata = createMetadata(macroParameters);
                return metadata;
            }
        }
        return null;
    }

    /**
	 * @see fr.xebia.confluence2wordpress.core.metadata.MetadataManager#storeMetadata(com.atlassian.confluence.core.ContentEntityObject, fr.xebia.confluence2wordpress.core.metadata.Metadata)
	 * {@inheritDoc}
	 */
    public void storeMetadata(ContentEntityObject page, Metadata metadata) throws MetadataException{
        String content = page.getBodyAsString();
    	Map<String, String> macroParameters = getMacroParameters(metadata);
        StringBuilder newContent = new StringBuilder();
        if(metadata.getPostId() != null && content.indexOf(WORDPRESS_SYNC_INFO) == -1) {
			newContent.append(WORDPRESS_SYNC_INFO);
        }
        StringBuilder metadataMacroBody = writeMetadataMacroBody(macroParameters);
        int start = content.indexOf(WORDPRESS_META_TAG_START);
        int end = start == -1 ? -1 : content.indexOf(WORDPRESS_META_TAG_END, start);
        if(start != -1 && end != -1){
        	newContent.append(content.substring(0, start));
        	newContent.append(metadataMacroBody);
        	newContent.append(content.substring(end + WORDPRESS_META_TAG_END.length()));
        } else {
        	newContent.append(content);
        	newContent.append(WORDPRESS_META_WRAP_START);
        	newContent.append(WORDPRESS_META_TAG_START);
        	newContent.append(metadataMacroBody);
        	newContent.append(WORDPRESS_META_WRAP_END);
        	newContent.append(WORDPRESS_META_TAG_END);
        }
        page.setBodyAsString(newContent.toString());
        pageLabelsSynchronizer.tagPage(page);
    }

    /**
	 * @see fr.xebia.confluence2wordpress.core.metadata.MetadataManager#createMetadata(com.atlassian.confluence.core.ContentEntityObject, java.util.Set, java.util.Set)
	 * {@inheritDoc}
	 */
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


    /**
	 * @see fr.xebia.confluence2wordpress.core.metadata.MetadataManager#createMetadata(java.util.Map)
	 * {@inheritDoc}
	 */
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

    /**
	 * @see fr.xebia.confluence2wordpress.core.metadata.MetadataManager#getMacroParameters(fr.xebia.confluence2wordpress.core.metadata.Metadata)
	 * {@inheritDoc}
	 */
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
    
    /**
	 * @see fr.xebia.confluence2wordpress.core.metadata.MetadataManager#readMetadataMacroBody(java.lang.String)
	 * {@inheritDoc}
	 */
    public Map<String, String> readMetadataMacroBody(String macroBody) {
        Map<String, String> macroParameters = new HashMap<String, String>();
        Iterator<String> iterator = ROW_SPLITTER.split(macroBody).iterator();
		while(iterator.hasNext()){
			String line = iterator.next();
			Matcher matcher = ROW_PATTERN.matcher(line);
			if(matcher.matches()){
                String key = StringUtils.trimToNull(matcher.group(1));
                String value = StringUtils.trimToNull(matcher.group(2));
                macroParameters.put(key, value);
			}
    	}
        return macroParameters;
    }

    /**
	 * @see fr.xebia.confluence2wordpress.core.metadata.MetadataManager#writeMetadataMacroBody(java.util.Map)
	 * {@inheritDoc}
	 */
    public StringBuilder writeMetadataMacroBody(Map<String,String> macroParameters) {
        StringBuilder sb = new StringBuilder();
        sb.append(WORDPRESS_META_BODY_START);
        for (Entry<String,String> entry : macroParameters.entrySet()) {
            if(entry.getValue() != null){
                sb.append(TABLE_LINE_START);
                sb.append(entry.getKey());
                sb.append(TABLE_LINE_MIDDLE);
                sb.append(entry.getValue());
                sb.append(TABLE_LINE_END);
            }
        } 
        sb.append(WORDPRESS_META_BODY_END);
        return sb;
    }


}