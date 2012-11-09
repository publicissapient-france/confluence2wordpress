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
/**
 * 
 */
package fr.dutra.confluence2wordpress.macro;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;

import fr.dutra.confluence2wordpress.core.author.Author;
import fr.dutra.confluence2wordpress.core.velocity.VelocityHelper;


public class AuthorMacro implements Macro {

	private VelocityHelper velocityHelper = new VelocityHelper();

	@Override
	public String execute(Map<String, String> paramMap, String paramString, ConversionContext paramConversionContext) throws MacroExecutionException {
		Author author;
		try {
			author = Author.fromMacroParameters(paramMap);
		} catch (MalformedURLException e) {
			throw new MacroExecutionException(e);
		} catch (URISyntaxException e) {
			throw new MacroExecutionException(e);
		}
		return velocityHelper.generateAuthorHtml(author);
	}

	@Override
	public BodyType getBodyType() {
		return BodyType.NONE;
	}

	@Override
	public OutputType getOutputType() {
		return OutputType.BLOCK;
	}

}
