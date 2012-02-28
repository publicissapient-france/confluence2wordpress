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
package fr.xebia.confluence2wordpress.core.converter;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.xhtml.api.XhtmlContent;


@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ConverterTest {
	
	@Mock
	private Renderer renderer;
	
    @Mock
    private XhtmlContent xhtmlUtils;
    
	@Mock
	private ContentEntityObject page;
	
	private Converter converter;

	@Before
	public void createConverter() throws Exception {
		converter = new DefaultConverter(renderer, xhtmlUtils);
	}

	@Test
	public void test_more_macro() throws ConversionException {
		when(renderer.render(any(String.class), any(ConversionContext.class))).thenReturn("<!--more-->");
		String html = converter.convert(page, new ConverterOptions());
		assertEquals("<!--more-->", StringUtils.trim(html));
	}

}
