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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.google.common.collect.Maps;

import fr.xebia.confluence2wordpress.wp.WordpressFile;

@RunWith(PowerMockRunner.class)
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

	private String json;
	
	@Before
	public void prepare() throws IOException {
		json = FileUtils.readFileToString(new File("src/test/resources/metadata.json"));
	}
	
	@Test
	public void testUnmarshalMetadata() throws Exception {
		MetadataManager m = new DefaultMetadataManager(xhtmlUtils);
		Metadata metadata = m.unmarshalMetadata(json);
		assertEquals(new Date(1330693920000L), metadata.getDateCreated());
		assertEquals("7e0cb48cb807c098d34206091c862d449652d1d6581e82b20e29f5e223f768fd", metadata.getDigest());
		assertEquals(true, metadata.isDraft());
		assertEquals("info", metadata.getIgnoredConfluenceMacros().get(1));
		assertEquals("Lorem ipsum", metadata.getPageTitle());
		assertEquals(564, metadata.getPostId().intValue());
		assertEquals("style=\"float:left\"", metadata.getTagAttributes().get("img"));
		assertEquals("/download/attachments/2588674/landscape.jpg", metadata.getAttachments().get(0).getAttachmentPath());
		assertEquals(false, metadata.isIncludeTOC());
		assertEquals(Arrays.asList(new String[]{"ipsum", "lorem"}), metadata.getTagNames());
	}
	
	@Test
	public void testMarshallMetadata() throws MetadataException {
		Metadata metadata = new Metadata();
		metadata.setIncludeTOC(true);
		metadata.setPostId(43);
		metadata.setTagNames(Arrays.asList(new String[]{"lorem", "ipsum"}));
		HashMap<String,String> map = Maps.newLinkedHashMap();
		map.put("img", "style=\"foo\" class='bar'");
		map.put("a", "");
		map.put("p", "alt=\"foo\"");
		metadata.setTagAttributes(map);
		metadata.setPermalink("http://foo.com/bar");
		MetadataManager m = new DefaultMetadataManager(xhtmlUtils);
		String json = m.marshalMetadata(metadata);
		
//		Map<String, String> macroParameters = m.getMacroParameters(metadata);
//		String body = m.writeMetadataMacroBody(macroParameters).toString();
//		assertTrue(body.contains(permalink));
//		assertTrue(body.contains(tags));
//		assertTrue(body.contains(postId));
//		assertTrue(body.contains(includeTOC));
//		assertTrue(body.contains(tagAttributes));
	}

}
