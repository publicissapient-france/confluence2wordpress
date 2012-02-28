package fr.xebia.confluence2wordpress.core.metadata;

import java.util.Set;

import com.atlassian.confluence.core.ContentEntityObject;

import fr.xebia.confluence2wordpress.wp.WordpressCategory;
import fr.xebia.confluence2wordpress.wp.WordpressUser;

public interface MetadataManager {

	String WORDPRESS_METADATA_MACRO_NAME = "wordpress-metadata";

	Metadata extractMetadata(ContentEntityObject page) throws MetadataException;

	void storeMetadata(ContentEntityObject page, Metadata metadata) throws MetadataException;

	Metadata createMetadata(ContentEntityObject page, Set<WordpressUser> users, Set<WordpressCategory> categories);

	Metadata unmarshalMetadata(String macroBody) throws MetadataException;

	String marshalMetadata(Metadata metadata) throws MetadataException;
	
}