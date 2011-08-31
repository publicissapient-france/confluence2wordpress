package fr.xebia.confluence2wordpress.core.metadata;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Map;

import org.junit.Test;

public class MetadataManagerTest {

	private String includeTOC = "    Include TOC : true\r\n";
	
	private String tags = " Tags : maven,mindmapping\r\n";
	
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
		metadata.setPermalink("http://wordpress.dutra.fr/2011/08/28/le-mind-mapping-applique-aux-dependances-des-projets-mavenises");
		Map<String, String> macroParameters = m.getMacroParameters(metadata);
		String body = m.writeMetadataMacroBody(macroParameters).toString();
		assertTrue(body.contains(permalink));
		assertTrue(body.contains(tags));
		assertTrue(body.contains(postId));
		assertTrue(body.contains(includeTOC));
	}

}
