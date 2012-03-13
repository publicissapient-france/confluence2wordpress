package fr.dutra.confluence2wordpress.core.sync;

import static com.atlassian.confluence.content.render.xhtml.XhtmlConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;

import fr.dutra.confluence2wordpress.core.metadata.Metadata;
import fr.dutra.confluence2wordpress.core.settings.PluginSettingsManager;
import fr.dutra.confluence2wordpress.util.StaxUtils;
import fr.dutra.confluence2wordpress.wp.WordpressClient;
import fr.dutra.confluence2wordpress.wp.WordpressFile;
import fr.dutra.confluence2wordpress.wp.WordpressXmlRpcException;


public class DefaultAttachmentsSynchronizer implements AttachmentsSynchronizer {

	/*
	 * <ac:image ac:title="foo.jpg">
	 *     <ri:attachment ri:filename="foo.jpg">
	 *         <ri:page ri:content-title="title" ri:space-key="sk" />
	 *     </ri:attachment>
	 * </ac:image>
	 */
	
    private static final QName ATTACHMENT_QNAME = new QName(RESOURCE_IDENTIFIER_NAMESPACE_URI, "attachment");

    private static final QName FILENAME_QNAME = new QName(RESOURCE_IDENTIFIER_NAMESPACE_URI, "filename");

    private static final QName PAGE_QNAME = new QName(RESOURCE_IDENTIFIER_NAMESPACE_URI, "page");

    private static final QName TITLE_QNAME = new QName(RESOURCE_IDENTIFIER_NAMESPACE_URI, "content-title");

    private static final QName SPACE_QNAME = new QName(RESOURCE_IDENTIFIER_NAMESPACE_URI, "space-key");

    private AttachmentManager attachmentManager;
    
    private PageManager pageManager;
    
    private PluginSettingsManager pluginSettingsManager;

	public DefaultAttachmentsSynchronizer(AttachmentManager attachmentManager, PageManager pageManager, PluginSettingsManager pluginSettingsManager) {
		super();
		this.attachmentManager = attachmentManager;
		this.pageManager = pageManager;
		this.pluginSettingsManager = pluginSettingsManager;
	}

	public List<SynchronizedAttachment> synchronizeAttachments(ContentEntityObject page, Metadata metadata) throws SynchronizationException, WordpressXmlRpcException {
		Set<Attachment> attachments = parseForAttachments(page);
        removeOldAttachments(metadata, attachments);
        if(attachments == null || attachments.isEmpty()){
            return null;
        }
        Set<Attachment> toUpload = findNewAttachments(metadata, attachments);
        List<SynchronizedAttachment> uploaded = uploadAttachments(toUpload);
        ArrayList<SynchronizedAttachment> newAttachments = mergeOldAndNewAttachments(metadata, uploaded);
		metadata.setAttachments(newAttachments);
		return newAttachments;
    }

    private Set<Attachment> parseForAttachments(ContentEntityObject page) throws SynchronizationException {
    	Set<Attachment> attachments = new HashSet<Attachment>();
        try {
			XMLEventReader r = StaxUtils.getReader(page);
			String fileName = null;
			String pageTitle = null;
			String spaceKey = null;
			try {
			    while(r.hasNext()){
			        XMLEvent e = r.nextEvent();
			        if(e.isStartElement()){
			        	StartElement startElement = e.asStartElement();
			        	QName name = startElement.getName();
						if(name.equals(ATTACHMENT_QNAME)) {
				            Attribute att = startElement.getAttributeByName(FILENAME_QNAME);
				            if(att != null){
				                fileName = att.getValue();
				            }
			        	} else if(name.equals(PAGE_QNAME)) {
				            Attribute title = startElement.getAttributeByName(TITLE_QNAME);
				            if(title != null){
				                pageTitle = title.getValue();
				            }
				            Attribute space = startElement.getAttributeByName(SPACE_QNAME);
				            if(space != null){
				                spaceKey = space.getValue();
				            }
			        	}
			        } else if (e.isEndElement()) {
			        	EndElement endElement = e.asEndElement();
			        	if(endElement.getName().equals(ATTACHMENT_QNAME)) {
			        		ContentEntityObject attachmentPage;
			        		if(pageTitle == null) {
			        			attachmentPage = page;
			        		} else {
			        			attachmentPage = pageManager.getPage(spaceKey, pageTitle);
			        		}
			                Attachment attachment = attachmentManager.getAttachment(attachmentPage, fileName);
			                attachments.add(attachment);
			                fileName = null;
			    			pageTitle = null;
			    			spaceKey = null;
			        	}
			        }
			    }
			} finally {
			    r.close();
			}
		} catch (XMLStreamException e) {
			throw new SynchronizationException("Cannot read page: " + page.getTitle(), e);
		}
        return attachments;
    }

	private void removeOldAttachments(Metadata metadata, Set<Attachment> attachments) {
		if(attachments == null || attachments.isEmpty()) {
			metadata.setAttachments(null);
        	return;
        }
		List<SynchronizedAttachment> metadataAttachments = metadata.getAttachments();
		if(metadataAttachments == null || metadataAttachments.isEmpty()) {
        	return;
        }
		Iterator<SynchronizedAttachment> it = metadataAttachments.iterator();
    	outer: while(it.hasNext()){
    		SynchronizedAttachment sa = it.next();
    		for (Attachment attachment : attachments) {
				if(attachment.getId() == sa.getAttachmentId()) {
					continue outer;
				}
			}
    		it.remove();
    	}
	}

	private Set<Attachment> findNewAttachments(Metadata metadata, Set<Attachment> attachments) {
		Set<Attachment> newAttachments = new HashSet<Attachment>();
		List<SynchronizedAttachment> metadataAttachments = metadata.getAttachments();
        if(metadataAttachments == null || metadataAttachments.isEmpty()) {
        	newAttachments.addAll(attachments);
        } else {
        	outer: for (Attachment attachment : attachments) {
        		for (SynchronizedAttachment metadataAttachment : metadataAttachments) {
					if(metadataAttachment.getAttachmentId() == attachment.getId()) {
		        		Integer version = metadataAttachment.getAttachmentVersion();
		        		if (version == null || attachment.getAttachmentVersion() > version){
		        			newAttachments.add(attachment);
		        		}
	        			continue outer;
					}
				}
    			newAttachments.add(attachment);
        	}
        }
		return newAttachments;
	}

	private ArrayList<SynchronizedAttachment> mergeOldAndNewAttachments(Metadata metadata, List<SynchronizedAttachment> uploaded) {
		Set<SynchronizedAttachment> metadataAttachments = new HashSet<SynchronizedAttachment>();
        //modified first
        if(uploaded != null) {
        	metadataAttachments.addAll(uploaded);
        }
        if(metadata.getAttachments() != null) {
        	metadataAttachments.addAll(metadata.getAttachments());
        }
        ArrayList<SynchronizedAttachment> newAttachments = new ArrayList<SynchronizedAttachment>(metadataAttachments);
		return newAttachments;
	}

    private List<SynchronizedAttachment> uploadAttachments(Set<Attachment> attachments) throws WordpressXmlRpcException, SynchronizationException {
        if(attachments == null || attachments.isEmpty()) {
            return null;
        }
        int size = attachments.size();
        final WordpressClient client = pluginSettingsManager.getWordpressClient();
        List<FutureHolder> futures = new ArrayList<FutureHolder>(size);
        for (final Attachment attachment : attachments) {
            byte[] data;
            try {
                data = IOUtils.toByteArray(attachment.getContentsAsStream());
            } catch (IOException e) {
                throw new SynchronizationException("Cannot read attachment: " + attachment.getFileName(), e);
            }
            WordpressFile file = new WordpressFile(
                attachment.getFileName(),
                attachment.getContentType(),
                data);
            futures.add(new FutureHolder(attachment, client.uploadFile(file)));
        }
        List<SynchronizedAttachment> synchronizedAttachments = new ArrayList<SynchronizedAttachment>(size);
        for (FutureHolder future : futures) {
        	try {
	            SynchronizedAttachment synchronizedAttachment = future.toSynchronizedAttachment();
                synchronizedAttachments.add(synchronizedAttachment);
	        } catch (InterruptedException e) {
	            throw new WordpressXmlRpcException("Cannot upload attachment", e);
	        } catch (ExecutionException e) {
	        	if(e.getCause() instanceof WordpressXmlRpcException) {
	        		throw (WordpressXmlRpcException) e.getCause();
	        	}
	        	throw new WordpressXmlRpcException("Cannot upload attachment", e.getCause() == null ? e : e.getCause());
	        }
		}
        return synchronizedAttachments;
    }
    
    private class FutureHolder {
    	private Attachment attachment;
    	private Future<WordpressFile> future;
		private FutureHolder(Attachment attachment, Future<WordpressFile> future) {
			super();
			this.attachment = attachment;
			this.future = future;
		}
    	private SynchronizedAttachment toSynchronizedAttachment() throws InterruptedException, ExecutionException{
    		return new SynchronizedAttachment(attachment, future.get());
    	}
    }
}
