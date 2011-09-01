package fr.xebia.confluence2wordpress.core.converter.postprocessors;

import org.htmlcleaner.TagNode;

import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;
import fr.xebia.confluence2wordpress.core.converter.VelocityHelper;
import fr.xebia.confluence2wordpress.core.converter.visitors.HeadingsCollector;


public class TableOfContentsPostProcessor implements PostProcessor {

    private VelocityHelper velocityHelper = new VelocityHelper();

    /* (non-Javadoc)
     * @see fr.xebia.confluence2wordpress.core.converter.postprocess.ConversionPostProcessor#postProcess(java.lang.String, org.htmlcleaner.TagNode, fr.xebia.confluence2wordpress.core.converter.ConverterOptions)
     */
    @Override
    public String postProcess(String html, TagNode body, ConverterOptions options) {
        if(options.isIncludeTOC() || options.isOptimizeForRDP()) {
            HeadingsCollector collector = new HeadingsCollector();
            body.traverse(collector);
            String toc = velocityHelper.generateTOC(collector.getHeadings());
            return toc + html;
        } else {
            return html;
        }
    }
}
