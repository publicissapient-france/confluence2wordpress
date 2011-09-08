/**
 * 
 */
package fr.xebia.confluence2wordpress.macro;

import java.util.Map;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.WysiwygBodyType;

import fr.xebia.confluence2wordpress.core.velocity.VelocityHelper;


/**
 *
 */
public class MoreMacro extends BaseMacro {

	private VelocityHelper velocityHelper = new VelocityHelper();
    
    @Override
    public String execute(@SuppressWarnings("rawtypes") Map parameters, String body, RenderContext renderContext) throws MacroException {
		return velocityHelper.generateReadMoreHtml();
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
