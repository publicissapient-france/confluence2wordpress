package fr.xebia.confluence2wordpress.core.metadata;

import java.util.Map;
import java.util.Set;

import com.atlassian.confluence.core.ContentEntityObject;

import fr.xebia.confluence2wordpress.wp.WordpressCategory;
import fr.xebia.confluence2wordpress.wp.WordpressUser;

public interface MetadataManager {

	Metadata extractMetadata(ContentEntityObject page) throws MetadataException;

	void storeMetadata(ContentEntityObject page, Metadata metadata) throws MetadataException;

	Metadata createMetadata(ContentEntityObject page, Set<WordpressUser> users, Set<WordpressCategory> categories);

	Metadata createMetadata(Map<String, String> macroParameters) throws MetadataException;

	Map<String, String> getMacroParameters(Metadata metadata) throws MetadataException;

	Map<String, String> readMetadataMacroBody(String macroBody);

	StringBuilder writeMetadataMacroBody(Map<String, String> macroParameters);

}