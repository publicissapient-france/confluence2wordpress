package fr.xebia.confluence2wordpress.util;

import static com.atlassian.confluence.content.render.xhtml.XhtmlConstants.*;

import java.io.StringReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import com.atlassian.confluence.core.ContentEntityObject;


public class StaxUtils {

    private static final XMLInputFactory FACTORY = XMLInputFactory.newInstance();

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
        return FACTORY.createXMLEventReader(new StringReader(XML_START + page.getBodyAsString() + XML_END));
    }
    
}
