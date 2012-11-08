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
package fr.dutra.confluence2wordpress.macro.legacy;

import java.util.Map;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;
import com.atlassian.user.User;
import com.opensymphony.webwork.ServletActionContext;

import fr.dutra.confluence2wordpress.core.metadata.Metadata;
import fr.dutra.confluence2wordpress.core.metadata.MetadataException;
import fr.dutra.confluence2wordpress.core.metadata.MetadataManager;
import fr.dutra.confluence2wordpress.core.permissions.PluginPermissionsManager;
import fr.dutra.confluence2wordpress.core.velocity.VelocityHelper;


/**
 *
 */
public class SyncInfoLegacyMacro extends BaseMacro {

	private VelocityHelper velocityHelper = new VelocityHelper();
	
	private MetadataManager metadataManager;
    
	private PluginPermissionsManager pluginPermissionsManager;
	
	private UserAccessor userAccessor;
	
    public SyncInfoLegacyMacro(MetadataManager metadataManager, PluginPermissionsManager pluginPermissionsManager, UserAccessor userAccessor) {
        super();
        this.metadataManager = metadataManager;
        this.pluginPermissionsManager = pluginPermissionsManager;
        this.userAccessor = userAccessor;
    }

    @Override
    public String execute(@SuppressWarnings("rawtypes") Map parameters, String body, RenderContext renderContext) throws MacroException {
		// retrieve a reference to the body object this macro is in
		if (!(renderContext instanceof PageContext)) {
			throw new MacroException("This macro can only be used in a page");
		}
        ContentEntityObject page = ((PageContext) renderContext).getEntity();
		Metadata metadata;
		try {
			metadata = metadataManager.extractMetadata(page);
			if(metadata == null){
				//macros cannot return null
			    return "";
			}
		} catch (MetadataException e) {
			throw new MacroException("Cannot extract Wordpress metadata", e);
		}
        User user;
        if(ServletActionContext.getRequest().getRemoteUser() == null){
            user = AuthenticatedUserThreadLocal.getUser();
        } else {
			user = userAccessor.getUser(ServletActionContext.getRequest().getRemoteUser());
        }
        boolean checkUsagePermission = pluginPermissionsManager.checkUsagePermission(user, page);
        return velocityHelper.generateMetadataHtml(page, checkUsagePermission, metadata);
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    @Override
    public TokenType getTokenType(@SuppressWarnings("rawtypes") Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    @Override
    public WysiwygBodyType getWysiwygBodyType() {
        return WysiwygBodyType.PREFORMAT;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public boolean suppressMacroRenderingDuringWysiwyg() {
        return false;
    }

    @Override
    public boolean suppressSurroundingTagDuringWysiwygRendering() {
        return false;
    }

}