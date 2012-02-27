package fr.xebia.confluence2wordpress.core.sync;

import java.util.List;

import com.atlassian.confluence.core.ContentEntityObject;

import fr.xebia.confluence2wordpress.core.metadata.Metadata;
import fr.xebia.confluence2wordpress.wp.WordpressXmlRpcException;

public interface AttachmentsSynchronizer {

	List<SynchronizedAttachment> synchronizeAttachments(ContentEntityObject page, Metadata metadata) throws SynchronizationException, WordpressXmlRpcException;

}