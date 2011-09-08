package fr.xebia.confluence2wordpress.core.velocity;

import java.util.List;
import java.util.Map;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;

import fr.xebia.confluence2wordpress.core.converter.visitors.Heading;
import fr.xebia.confluence2wordpress.core.metadata.Metadata;


public class VelocityHelper {

    private static final String MORE_VM = "/vm/more.vm";
    
	private static final String SYNC_INFO_VM = "/vm/sync-info.vm";
	
	private static final String TOC_VM = "/vm/toc.vm";
	
	private static final String RDP_HEADER_VM = "/vm/rdp-header.vm";

    public String generateHeader() {
        // Create the Velocity Context
        Map<String,Object> context = MacroUtils.defaultVelocityContext();
        // Render the Template
        String result = VelocityUtils.getRenderedTemplate(RDP_HEADER_VM, context);
        return result;
    }

    public String generateTOC(List<Heading> headings) {
        // Create the Velocity Context
        Map<String,Object> context = MacroUtils.defaultVelocityContext();
        context.put("headings", headings);
        // Render the Template
        String result = VelocityUtils.getRenderedTemplate(TOC_VM, context);
        return result;
    }
    
    public String generateMetadataHtml(ContentEntityObject page, boolean userHasPluginUsagePermission, Metadata metadata) {
    	Map<String,Object> context = MacroUtils.defaultVelocityContext();
        context.put("page", page);
        context.put("metadata", metadata);
        context.put("userHasPluginUsagePermission", userHasPluginUsagePermission);
        // Render the Template
        String result = VelocityUtils.getRenderedTemplate(SYNC_INFO_VM, context);
        return result;
    }

    public String generateReadMoreHtml() {
		// Create the Velocity Context
        Map<String,Object> context = MacroUtils.defaultVelocityContext();
        // Render the Template
        String result = VelocityUtils.getRenderedTemplate(MORE_VM, context);
        return result;
	}

}
