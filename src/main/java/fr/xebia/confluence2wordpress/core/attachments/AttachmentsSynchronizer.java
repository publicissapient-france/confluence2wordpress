package fr.xebia.confluence2wordpress.core.attachments;

import static com.atlassian.confluence.content.render.xhtml.XhtmlConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.io.IOUtils;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;

import fr.xebia.confluence2wordpress.core.settings.PluginSettingsManager;
import fr.xebia.confluence2wordpress.util.StaxUtils;
import fr.xebia.confluence2wordpress.wp.WordpressClient;
import fr.xebia.confluence2wordpress.wp.WordpressFile;
import fr.xebia.confluence2wordpress.wp.WordpressXmlRpcException;


public class AttachmentsSynchronizer {

    private static final QName FILENAME_QNAME = new QName(RESOURCE_IDENTIFIER_NAMESPACE_URI, "filename");

    private AttachmentManager attachmentManager;
    
    private PluginSettingsManager pluginSettingsManager;

    public List<SynchronizedAttachment> synchronizeAttachments(ContentEntityObject page) throws WordpressXmlRpcException, XMLStreamException {
        List<Attachment> attachments = parseForAttachmentsToUpload(page);
        if(attachments == null || attachments.isEmpty()){
            return null;
        }
        return uploadAttachments(page);
    }

    private List<Attachment> parseForAttachmentsToUpload(ContentEntityObject page) throws XMLStreamException {
        List<Attachment> attachments = attachmentManager.getAttachments(page);
        if(attachments == null || attachments.isEmpty()) {
            return null;
        }
        attachments = new ArrayList<Attachment>();
        XMLEventReader r = StaxUtils.getReader(page);
        try {
            while(r.hasNext()){
                XMLEvent e = r.nextEvent();
                if(e.isAttribute()){
                    Attribute att = (Attribute) e;
                    if(att.getName().equals(FILENAME_QNAME)){
                        String attachmentFileName = att.getValue();
                        Attachment attachment = attachmentManager.getAttachment(page, attachmentFileName);
                        attachments.add(attachment);
                        
                    }
                }
            }
        } finally {
            r.close();
        }
        return attachments;
    }

    private List<SynchronizedAttachment> uploadAttachments(ContentEntityObject page) throws WordpressXmlRpcException{
        List<Attachment> attachments = attachmentManager.getAttachments(page);
        if(attachments == null || attachments.isEmpty()) {
            return null;
        }
        int size = attachments.size();
        final WordpressClient client = pluginSettingsManager.getWordpressClient();
        List<Future<WordpressFile>> files = new ArrayList<Future<WordpressFile>>(size);
        for (final Attachment attachment : attachments) {
            byte[] data;
            try {
                data = IOUtils.toByteArray(attachment.getContentsAsStream());
            } catch (IOException e) {
                throw new WordpressXmlRpcException("Cannot read attachment: " + attachment.getFileName(), e);
            }
            WordpressFile file = new WordpressFile(
                attachment.getFileName(),
                attachment.getContentType(),
                data);
            files.add(client.uploadFile(file));
        }
        List<SynchronizedAttachment> synchronizedAttachments = new ArrayList<SynchronizedAttachment>(size);
        for (int i = 0; i < size; i++) {
            WordpressFile wordpressFile;
            try {
                wordpressFile = files.get(i).get();
            } catch (InterruptedException e) {
                throw new WordpressXmlRpcException("Cannot upload attachment", e);
            } catch (ExecutionException e) {
                if(e.getCause() instanceof WordpressXmlRpcException){
                    throw (WordpressXmlRpcException) e.getCause();
                }
                throw new WordpressXmlRpcException("Cannot upload attachment", e.getCause() == null ? e : e.getCause());
            }
            synchronizedAttachments.add(new SynchronizedAttachment(attachments.get(i), wordpressFile));
        }

        return synchronizedAttachments;
    }
}
