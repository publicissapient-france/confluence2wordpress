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
package fr.dutra.confluence2wordpress.core.metadata;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import fr.dutra.confluence2wordpress.core.metadata.Metadata;
import fr.dutra.confluence2wordpress.core.metadata.MetadataException;
import fr.dutra.confluence2wordpress.core.metadata.MetadataManager;

public class MetadataManagerTest {

	private String includeTOC = "    Include TOC : true\r\n";
	
	private String tags = " Tags : maven,mindmapping\r\n";
	
	private String tagAttributes = " Tag Attributes : img=\"style=\"\"foo\"\" class='bar'\",a=,p=\"alt=\"\"foo\"\"\"\r\n";
	
	private String postId = "    Post ID : 43\r\n";
	
	private String permalink = "    Permalink : http://wordpress.dutra.fr/2011/08/28/le-mind-mapping-applique-aux-dependances-des-projets-mavenises\r\n";
	
	private String macroBody = 
		"{details:label=WordpressMetadata|hidden=true}\r\n" +
		"    ## PLEASE DO NOT EDIT THIS SECTION MANUALLY!\r\n" +
		includeTOC +
		tags +
		postId +
		permalink +
		"{details}";
	
	@Test
	public void testReadMetadataMacroBody() throws MetadataException {
		MetadataManager m = new MetadataManager();
		Map<String, String> macroParameters = m.readMetadataMacroBody(macroBody);
		Metadata metadata = m.createMetadata(macroParameters);
		assertEquals(true, metadata.isIncludeTOC());
		assertEquals(Arrays.asList(new String[]{"maven", "mindmapping"}), metadata.getTagNames());
		assertEquals(43, metadata.getPostId().intValue());
	}
	
	@Test
	public void testWriteMetadataMacroBody() throws MetadataException {
		MetadataManager m = new MetadataManager();
		Metadata metadata = new Metadata();
		metadata.setIncludeTOC(true);
		metadata.setPostId(43);
		metadata.setTagNames(Arrays.asList(new String[]{"maven", "mindmapping"}));
		HashMap<String,String> map = Maps.newLinkedHashMap();
		map.put("img", "style=\"foo\" class='bar'");
		map.put("a", "");
		map.put("p", "alt=\"foo\"");
		metadata.setTagAttributes(map);
		metadata.setPermalink("http://wordpress.dutra.fr/2011/08/28/le-mind-mapping-applique-aux-dependances-des-projets-mavenises");
		Map<String, String> macroParameters = m.getMacroParameters(metadata);
		String body = m.writeMetadataMacroBody(macroParameters).toString();
		assertTrue(body.contains(permalink));
		assertTrue(body.contains(tags));
		assertTrue(body.contains(postId));
		assertTrue(body.contains(includeTOC));
		assertTrue(body.contains(tagAttributes));
	}

}
