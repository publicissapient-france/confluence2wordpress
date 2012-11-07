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
/**
 * 
 */
package fr.dutra.confluence2wordpress.core.converter.processors;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.google.common.base.Joiner;

import fr.dutra.confluence2wordpress.macro.Author;


/**
 * @author Alexandre Dutra
 *
 */
public class AuthorMacroProcessor extends MacroToMacroProcessor {

    private static final String CONFLUENCE_MACRO_NAME = "author";

    private static final String WORDPRESS_MACRO_NAME = "author";

	private static final Joiner JOINER = Joiner.on(" ").skipNulls();

	public AuthorMacroProcessor(XhtmlContent xhtmlUtils, ConversionContext conversionContext) {
        super(xhtmlUtils, conversionContext);
    }

    @Override
	protected String getConfluenceMacroName() {
		return CONFLUENCE_MACRO_NAME;
	}

	@Override
	protected String getWordpressMacroName(MacroDefinition macroDefinition) {
		return WORDPRESS_MACRO_NAME;
	}

	@Override
	protected Map<String, String> getWordpressMacroParameters(MacroDefinition macroDefinition) throws XhtmlException {
		Author author;
		try {
			author = Author.fromMacroParameters(macroDefinition.getParameters());
		} catch (MalformedURLException e) {
			throw new XhtmlException(e);
		} catch (URISyntaxException e) {
			throw new XhtmlException(e);
		}
		Map<String,String> params = new HashMap<String, String>();
		params.put("firstname", author.getFirstName());
		params.put("lastname", author.getLastName());
		params.put("username", author.getWordpressUsername());
		params.put("twitter", author.getTwitterAccount());
		params.put("urls", JOINER.join(author.getOtherUrls()));
		return params;
	}

}
