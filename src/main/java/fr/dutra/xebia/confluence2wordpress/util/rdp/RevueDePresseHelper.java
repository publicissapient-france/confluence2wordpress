package fr.dutra.xebia.confluence2wordpress.util.rdp;

import java.util.List;
import java.util.Map;

import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;


public class RevueDePresseHelper {

    public String generateHeader(List<Heading> headings) {
        // Create the Velocity Context
        Map<String,Object> context = MacroUtils.defaultVelocityContext();
        context.put("headings", headings);
        // Render the Template
        String result = VelocityUtils.getRenderedTemplate("/vm/rdp-header.vm", context);
        return result;
    }

}
