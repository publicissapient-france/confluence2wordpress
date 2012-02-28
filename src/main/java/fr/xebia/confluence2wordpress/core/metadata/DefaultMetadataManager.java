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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

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
	//see com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants
	  
	private static final String WORDPRESS_META_TAG_START = "<ac:macro ac:name=\""+ WORDPRESS_METADATA_MACRO_NAME + "\">";

    private static final String WORDPRESS_META_TAG_END = "</ac:macro>";

	private static final String BODY_TAG_START = "<ac:plain-text-body><![CDATA[";

    private static final String BODY_TAG_END = "]]></ac:plain-text-body>";

    private static final Pattern DRAFT_PREFIX_PATTERN = Pattern.compile("(DRAFT\\s*-\\s*).+");

	private static final String RDP_CATEGORY_NAME = "Revue de presse";

	private static final String RDP_PAGE_TITLE = "Revue de Presse Xebia";

	private static final String RDP_POST_SLUG_FORMAT = "revue-de-presse-xebia-%1$tY-%2$02d";

	private static final String XEBIA_FRANCE_LOGIN = "XebiaFrance";
	
	private final XhtmlContent xhtmlUtils;

	private final ObjectMapper mapper;

    public DefaultMetadataManager(XhtmlContent xhtmlUtils) {
		super();
		this.xhtmlUtils = xhtmlUtils;
    	this.mapper = new ObjectMapper();
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
		String macroBody = metadataMacro.getBodyText();
		if(StringUtils.isEmpty(macroBody)) {
			return null;
		}
		return unmarshalMetadata(macroBody);
    }
    
    public void storeMetadata(ContentEntityObject page, Metadata metadata) throws MetadataException{
        String content = page.getBodyAsString();
        StringBuilder macro = new StringBuilder();
        macro.append(WORDPRESS_META_TAG_START);
        macro.append(BODY_TAG_START);
    	String macroBody = marshalMetadata(metadata);
        macro.append(macroBody);
        macro.append(BODY_TAG_END);
        macro.append(WORDPRESS_META_TAG_END);
        int start = content.indexOf(WORDPRESS_META_TAG_START);
        int end = start == -1 ? -1 : content.indexOf(WORDPRESS_META_TAG_END, start);
        StringBuilder newContent = new StringBuilder();
        if(start != -1 && end != -1){
        	newContent.append(content.substring(0, start));
        	newContent.append(macro);
        	newContent.append(content.substring(end + WORDPRESS_META_TAG_END.length()));
        } else {
        	newContent.append(macro);
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

    public Metadata unmarshalMetadata(String macroBody) throws MetadataException {
		try {
			return mapper.readValue(macroBody, Metadata.class);
		} catch (JsonParseException e) {
			throw new MetadataException("Cannot unmarshal macro body: " + macroBody, e);
		} catch (JsonMappingException e) {
			throw new MetadataException("Cannot unmarshal macro body: " + macroBody, e);
		} catch (IOException e) {
			throw new MetadataException("Cannot unmarshal macro body: " + macroBody, e);
		}
    }

    public String marshalMetadata(Metadata metadata) throws MetadataException {
		try {
			return mapper.writeValueAsString(metadata);
		} catch (JsonParseException e) {
			throw new MetadataException("Cannot marshal metadata: " + metadata, e);
		} catch (JsonMappingException e) {
			throw new MetadataException("Cannot marshal metadata: " + metadata, e);
		} catch (IOException e) {
			throw new MetadataException("Cannot marshal metadata: " + metadata, e);
		}
    }
}