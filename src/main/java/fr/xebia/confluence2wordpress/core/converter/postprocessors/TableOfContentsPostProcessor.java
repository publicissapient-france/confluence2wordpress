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
package fr.xebia.confluence2wordpress.core.converter.postprocessors;

import org.htmlcleaner.TagNode;

import fr.xebia.confluence2wordpress.core.converter.ConverterOptions;
import fr.xebia.confluence2wordpress.core.converter.visitors.HeadingsCollector;
import fr.xebia.confluence2wordpress.core.velocity.VelocityHelper;


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
