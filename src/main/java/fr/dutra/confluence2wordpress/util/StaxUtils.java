package fr.dutra.confluence2wordpress.util;

import static com.atlassian.confluence.content.render.xhtml.XhtmlConstants.*;

import java.io.StringReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import com.atlassian.confluence.core.ContentEntityObject;


public class StaxUtils {

	/*
	 * see  com.atlassian.confluence.content.render.xhtml.DefaultXmlEventReaderFactory
	 */
	
    private static final XMLInputFactory INPUT_FACTORY;
    
    static {
    	INPUT_FACTORY = XMLInputFactory.newInstance();
    	INPUT_FACTORY.setProperty("javax.xml.stream.supportDTD", Boolean.TRUE);
    	INPUT_FACTORY.setProperty("javax.xml.stream.isCoalescing", Boolean.FALSE);
    	INPUT_FACTORY.setProperty("javax.xml.stream.isReplacingEntityReferences", Boolean.FALSE);
    	INPUT_FACTORY.setProperty("com.ctc.wstx.normalizeAttrValues", Boolean.FALSE);
    }

    private static final String XML_START = 
        "<xml " +
        "xmlns=\"" + XHTML_NAMESPACE_URI + "\" " +
        "xmlns:"+CONFLUENCE_XHTML_NAMESPACE_PREFIX+"=\""+CONFLUENCE_XHTML_NAMESPACE_URI+"\" "+
        "xmlns:"+CONFLUENCE_XHTML_NAMESPACE_ALTERNATE_PREFIX+"=\""+CONFLUENCE_XHTML_NAMESPACE_URI+"\" "+
        "xmlns:"+RESOURCE_IDENTIFIER_NAMESPACE_PREFIX+"=\""+RESOURCE_IDENTIFIER_NAMESPACE_URI+"\" " +
        ">" ;
    
    private static final String XML_END = 
        "</xml>" ;

    public static XMLEventReader getReader(ContentEntityObject page) throws XMLStreamException{
        return getReader(page.getBodyAsString());
    }
    
    public static XMLEventReader getReader(String storage) throws XMLStreamException {
        return INPUT_FACTORY.createXMLEventReader(new StringReader(XML_START + storage + XML_END));
    }
}
