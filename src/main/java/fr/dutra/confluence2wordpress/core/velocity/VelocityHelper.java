/**
 * Copyright 2011-2012 Alexandre Dutra
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
package fr.dutra.confluence2wordpress.core.velocity;

import java.util.Map;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;

import fr.dutra.confluence2wordpress.core.author.Author;
import fr.dutra.confluence2wordpress.core.metadata.Metadata;
import fr.dutra.confluence2wordpress.core.toc.Heading;


public class VelocityHelper {

    private static final String MORE_VM = "/vm/more.vm";

    private static final String AUTHOR_VM = "/vm/author.vm";
    
	private static final String SYNC_INFO_VM = "/vm/sync-info.vm";

	private static final String TOC_VM = "/vm/toc.vm";
	
	private static final String RDP_HEADER_VM = "/vm/rdp-header.vm";

    public String generatePressReviewHeader() {
        Map<String,Object> context = MacroUtils.defaultVelocityContext();
        return VelocityUtils.getRenderedTemplate(RDP_HEADER_VM, context);
    }

    public String generateTOC(Heading toc, Boolean ordered, String pageUrl) {
        Map<String,Object> context = MacroUtils.defaultVelocityContext();
        context.put("toc", toc);
        context.put("ordered", ordered);
        context.put("pageUrl", pageUrl);
        return VelocityUtils.getRenderedTemplate(TOC_VM, context);
    }
    
    public String generateMetadataHtml(ContentEntityObject page, boolean userHasPluginUsagePermission, Metadata metadata) {
    	Map<String,Object> context = MacroUtils.defaultVelocityContext();
        context.put("page", page);
        context.put("metadata", metadata);
        context.put("userHasPluginUsagePermission", userHasPluginUsagePermission);
        return VelocityUtils.getRenderedTemplate(SYNC_INFO_VM, context);
    }

    public String generateReadMoreHtml() {
        Map<String,Object> context = MacroUtils.defaultVelocityContext();
        return VelocityUtils.getRenderedTemplate(MORE_VM, context);
	}

    public String generateAuthorHtml(Author author) {
        Map<String,Object> context = MacroUtils.defaultVelocityContext();
        context.put("author", author);
        return VelocityUtils.getRenderedTemplate(AUTHOR_VM, context);
	}

}
