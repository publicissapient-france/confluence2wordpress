/**
 * 
 */
package fr.xebia.confluence2wordpress.macro;

import java.util.Map;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;

import fr.xebia.confluence2wordpress.core.converter.VelocityHelper;


/**
 *
 */
public class MetadataMacro extends BaseMacro {

	private VelocityHelper velocityHelper = new VelocityHelper();
    
	@SuppressWarnings("unchecked")
	@Override
    public String execute(@SuppressWarnings("rawtypes") Map parameters, String body, RenderContext renderContext) throws MacroException {
		// retrieve a reference to the body object this macro is in
		if (!(renderContext instanceof PageContext)) {
			throw new MacroException("This macro can only be used in a page");
		}
		ContentEntityObject page = ((PageContext) renderContext).getEntity();
		return velocityHelper.generateMetadataHtml(parameters, page);
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
