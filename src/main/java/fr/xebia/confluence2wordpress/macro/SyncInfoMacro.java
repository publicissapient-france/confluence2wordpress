/**
 * 
 */
package fr.xebia.confluence2wordpress.macro;

import java.util.Map;

import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;
import com.atlassian.user.User;
import com.opensymphony.webwork.ServletActionContext;

import fr.xebia.confluence2wordpress.core.metadata.Metadata;
import fr.xebia.confluence2wordpress.core.metadata.MetadataException;
import fr.xebia.confluence2wordpress.core.metadata.MetadataManager;
import fr.xebia.confluence2wordpress.core.permissions.PluginPermissionsManager;
import fr.xebia.confluence2wordpress.core.velocity.VelocityHelper;


/**
 *
 */
public class SyncInfoMacro extends BaseMacro {

	private VelocityHelper velocityHelper = new VelocityHelper();
	
	private MetadataManager metadataManager = new MetadataManager();
    
	private PluginPermissionsManager pluginPermissionsManager;
	
    public SyncInfoMacro(PluginPermissionsManager pluginPermissionsManager) {
        super();
        this.pluginPermissionsManager = pluginPermissionsManager;
    }

    @Override
    public String execute(@SuppressWarnings("rawtypes") Map parameters, String body, RenderContext renderContext) throws MacroException {
		// retrieve a reference to the body object this macro is in
		if (!(renderContext instanceof PageContext)) {
			throw new MacroException("This macro can only be used in a page");
		}
        SpaceContentEntityObject page = (SpaceContentEntityObject) ((PageContext) renderContext).getEntity();
		Metadata metadata;
		try {
			metadata = metadataManager.extractMetadata(page);
			if(metadata == null){
			    return null;
			}
		} catch (MetadataException e) {
			throw new MacroException("Cannot extract Wordpress metadata", e);
		}
        User user;
        if(ServletActionContext.getRequest().getRemoteUser() == null){
            user = AuthenticatedUserThreadLocal.getUser();
        } else {
            user = page.getUserAccessor().getUser(ServletActionContext.getRequest().getRemoteUser());
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
