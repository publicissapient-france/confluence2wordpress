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
package fr.dutra.confluence2wordpress.macro;

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
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
public class SyncInfoMacro implements Macro {

	private VelocityHelper velocityHelper = new VelocityHelper();
	
	private MetadataManager metadataManager;
    
	private PluginPermissionsManager pluginPermissionsManager;
	
	private UserAccessor userAccessor;
	
    public SyncInfoMacro(MetadataManager metadataManager, PluginPermissionsManager pluginPermissionsManager, UserAccessor userAccessor) {
        super();
        this.metadataManager = metadataManager;
        this.pluginPermissionsManager = pluginPermissionsManager;
        this.userAccessor = userAccessor;
    }

	@Override
	public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        ContentEntityObject page = context.getEntity();
		Metadata metadata;
		try {
			metadata = metadataManager.extractMetadata(page);
		} catch (MetadataException e) {
			throw new MacroExecutionException("Cannot extract Wordpress metadata", e);
		}
        User user;
        if(ServletActionContext.getRequest() == null || ServletActionContext.getRequest().getRemoteUser() == null){
            user = AuthenticatedUserThreadLocal.getUser();
        } else {
			user = userAccessor.getUser(ServletActionContext.getRequest().getRemoteUser());
        }
        boolean checkUsagePermission = pluginPermissionsManager.checkUsagePermission(user, page);
        return velocityHelper.generateMetadataHtml(page, checkUsagePermission, metadata);
	}

	@Override
	public BodyType getBodyType() {
		return BodyType.NONE;
	}

	@Override
	public OutputType getOutputType() {
		return OutputType.BLOCK;
	}

}
