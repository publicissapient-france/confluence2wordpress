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
package fr.xebia.confluence2wordpress.core.metadata;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class MetadataManagerTest {

	private static final String WORDPRESS_META_TAG_START = "<ac:macro ac:name=\"wordpress-metadata\">";

    private static final String WORDPRESS_META_TAG_END = "</ac:macro>";

	private String includeTOC = "<ac:parameter ac:name=\"includeTOC\">true</ac:parameter>";
	
	private String tags = "<ac:parameter ac:name=\"tagNames\">maven,mindmapping</ac:parameter>";
	
	private String tagAttributes = "<ac:parameter ac:name=\"tagAttributes\">img=\"style=\"\"foo\"\" class='bar'\",a=,p=\"alt=\"\"foo\"\"\"</ac:parameter>";
	
	private String postId = "<ac:parameter ac:name=\"postId\">43</ac:parameter>";
	
	private String permalink = "<ac:parameter ac:name=\"permalink\">http://wordpress.dutra.fr/2011/08/28/le-mind-mapping-applique-aux-dependances-des-projets-mavenises</ac:parameter>";
	
	private String macroBody = 
		WORDPRESS_META_TAG_START +
		includeTOC +
		tags +
		postId +
		permalink +
		tagAttributes +
		WORDPRESS_META_TAG_END;
	
	private String body = "foo" + macroBody + "<ac:macro ac:name=\"foo\"></ac:macro>";

    @Mock
    private XhtmlContent xhtmlUtils;

	@Mock
	private ContentEntityObject page;
	
	@Test
	public void testReadMetadataMacroBody() throws MetadataException {
		when(page.getBodyAsString()).thenReturn(body);
		MetadataManager m = new DefaultMetadataManager(xhtmlUtils);
		Metadata metadata = m.extractMetadata(page);
		assertEquals(true, metadata.isIncludeTOC());
		assertEquals(Arrays.asList(new String[]{"maven", "mindmapping"}), metadata.getTagNames());
		assertEquals(43, metadata.getPostId().intValue());
	}
	
	@Test
	public void testWriteMetadataMacroBody() throws MetadataException {
//		MetadataManager m = new DefaultMetadataManager(xhtmlUtils);
//		Metadata metadata = new Metadata();
//		metadata.setIncludeTOC(true);
//		metadata.setPostId(43);
//		metadata.setTagNames(Arrays.asList(new String[]{"maven", "mindmapping"}));
//		HashMap<String,String> map = Maps.newLinkedHashMap();
//		map.put("img", "style=\"foo\" class='bar'");
//		map.put("a", "");
//		map.put("p", "alt=\"foo\"");
//		metadata.setTagAttributes(map);
//		metadata.setPermalink("http://wordpress.dutra.fr/2011/08/28/le-mind-mapping-applique-aux-dependances-des-projets-mavenises");
//		Map<String, String> macroParameters = m.getMacroParameters(metadata);
//		String body = m.writeMetadataMacroBody(macroParameters).toString();
//		assertTrue(body.contains(permalink));
//		assertTrue(body.contains(tags));
//		assertTrue(body.contains(postId));
//		assertTrue(body.contains(includeTOC));
//		assertTrue(body.contains(tagAttributes));
	}

}
