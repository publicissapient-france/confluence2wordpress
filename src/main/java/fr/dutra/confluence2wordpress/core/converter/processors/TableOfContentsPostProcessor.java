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
package fr.dutra.confluence2wordpress.core.converter.processors;

import java.util.List;

import org.htmlcleaner.TagNode;

import fr.dutra.confluence2wordpress.core.converter.ConverterOptions;
import fr.dutra.confluence2wordpress.core.velocity.VelocityHelper;


public class TableOfContentsPostProcessor implements PostProcessor {

    private final VelocityHelper velocityHelper = new VelocityHelper();

    private final List<Heading> headings;
    
	public TableOfContentsPostProcessor(List<Heading> headings) {
		this.headings = headings;
	}

	/* (non-Javadoc)
     * @see fr.dutra.confluence2wordpress.core.converter.postprocess.ConversionPostProcessor#postProcess(java.lang.String, org.htmlcleaner.TagNode, fr.dutra.confluence2wordpress.core.converter.ConverterOptions)
     */
    @Override
    public String postProcess(String html, TagNode body, ConverterOptions options) {
        String toc = velocityHelper.generateTOC(headings);
        return toc + html;
    }
}
