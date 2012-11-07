package fr.dutra.confluence2wordpress.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static junit.framework.Assert.*;

import org.junit.Test;

import fr.dutra.confluence2wordpress.util.UrlUtils;



public class UrlUtilsTest {

    @Test
    public void testExtractConfluenceRelativePath() throws MalformedURLException, URISyntaxException {

        assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "/confluence/download/attachments/983042/image.png?version=1&amp;modificationDate=1327402370523", 
                "http://localhost/confluence"));

        assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "http://localhost:1990/confluence/download/attachments/983042/image.png", 
                "http://localhost:1990/confluence"));

        assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "/confluence/download/attachments/983042/image.png?version=1&amp;modificationDate=1327402370523", 
                "http://localhost/confluence/"));

        assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "http://localhost:1990/confluence/download/attachments/983042/image.png", 
                "http://localhost:1990/confluence/"));

        assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "/download/attachments/983042/image.png?version=1&amp;modificationDate=1327402370523", 
                "http://localhost"));

        assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "http://localhost:1990/download/attachments/983042/image.png", 
                "http://localhost:1990"));


    }
    
    @Test
    public void testSanitize() throws MalformedURLException, URISyntaxException {

        try {
			assertEquals(null, UrlUtils.sanitize(null));
			fail();
		} catch (MalformedURLException e) {
		}

        try {
			assertEquals("", UrlUtils.sanitize(""));
			fail();
		} catch (MalformedURLException e) {
		}

        try {
			assertEquals("not really an URL", UrlUtils.sanitize(""));
			fail();
		} catch (MalformedURLException e) {
		}
        
        assertEquals(
            "http://foo.com?bar=%22S%C3%A3o%20Paulo%22", 
            UrlUtils.sanitize("http://foo.com?bar=\"São Paulo\""));

    }
}
