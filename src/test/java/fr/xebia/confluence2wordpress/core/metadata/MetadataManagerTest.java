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

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.google.common.collect.Maps;

import fr.xebia.confluence2wordpress.util.CodecUtils;

@RunWith(MockitoJUnitRunner.class)
public class MetadataManagerTest {

    @Mock
    private XhtmlContent xhtmlUtils;

	private String json;
	
	@Before
	public void prepare() throws IOException {
		json = FileUtils.readFileToString(new File("src/test/resources/metadata.json"));
	}
	
	@Test
	public void testUnmarshalMetadata() throws Exception {
		MetadataManager m = new DefaultMetadataManager(xhtmlUtils);
		Metadata metadata = m.unmarshalMetadata(CodecUtils.compressAndEncode(json));
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
	public void testMarshallMetadata() throws Exception {
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
		json = CodecUtils.decodeAndExpand(json);
		assertTrue(json.contains("\"tagNames\":[\"lorem\",\"ipsum\"]"));
		assertTrue(json.contains("\"tagAttributes\":{\"img\":\"style=\\\"foo\\\" class='bar'\",\"a\":\"\",\"p\":\"alt=\\\"foo\\\"\"}"));
		assertTrue(json.contains("\"permalink\":\"http://foo.com/bar\""));
	}

}
