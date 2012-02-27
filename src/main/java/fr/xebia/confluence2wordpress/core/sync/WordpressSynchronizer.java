package fr.xebia.confluence2wordpress.core.sync;

import com.atlassian.confluence.core.ContentEntityObject;

import fr.xebia.confluence2wordpress.core.converter.ConversionException;
import fr.xebia.confluence2wordpress.core.metadata.Metadata;
import fr.xebia.confluence2wordpress.wp.WordpressXmlRpcException;

public interface WordpressSynchronizer {

	Metadata synchronize(ContentEntityObject page, Metadata metadata) throws WordpressXmlRpcException, ConversionException, SynchronizationException;

	String preview(ContentEntityObject page, Metadata metadata) throws ConversionException;

}