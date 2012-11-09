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
package fr.dutra.confluence2wordpress.core.metadata;

import java.util.Set;

import com.atlassian.confluence.core.ContentEntityObject;

import fr.dutra.confluence2wordpress.wp.WordpressCategory;
import fr.dutra.confluence2wordpress.wp.WordpressUser;

public interface MetadataManager {

	String WORDPRESS_METADATA_MACRO_NAME = "wordpress-metadata";

	String WORDPRESS_SYNC_INFO_MACRO_NAME = "wordpress-sync-info";

	Metadata extractMetadata(ContentEntityObject page) throws MetadataException;

	void storeMetadata(ContentEntityObject page, Metadata metadata) throws MetadataException;

	Metadata createMetadata(ContentEntityObject page, Set<WordpressUser> users, Set<WordpressCategory> categories);

	Metadata unmarshalMetadata(String macroBody) throws MetadataException;

	String marshalMetadata(Metadata metadata) throws MetadataException;
	
}