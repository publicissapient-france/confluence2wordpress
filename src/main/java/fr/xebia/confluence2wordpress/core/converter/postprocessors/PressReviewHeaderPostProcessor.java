package fr.xebia.confluence2wordpress.core.converter.postprocessors;

import org.htmlcleaner.TagNode;

import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;
import fr.xebia.confluence2wordpress.core.converter.VelocityHelper;


public class PressReviewHeaderPostProcessor implements PostProcessor {
    
    private VelocityHelper velocityHelper = new VelocityHelper();

    public String postProcess(String html, TagNode body, ConverterOptions options) {
        if(options.isOptimizeForRDP()) {
            String header = velocityHelper.generateHeader();
            return header + html;
        } else {
            return html;
        }
    }
}
