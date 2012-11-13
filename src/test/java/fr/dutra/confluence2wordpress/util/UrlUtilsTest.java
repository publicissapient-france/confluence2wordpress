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
package fr.dutra.confluence2wordpress.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static junit.framework.Assert.*;

import org.junit.Ignore;
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
    public void testAbsolutize() throws MalformedURLException, URISyntaxException {

        assertEquals("http://foo.com/confluence/foo/bar", 
        		UrlUtils.absolutize("/confluence/foo/bar", "http://foo.com/confluence/"));

        assertEquals("http://foo.com/confluence/foo/bar", 
        		UrlUtils.absolutize("http://foo.com/confluence/foo/bar", "http://foo.com/confluence/"));

        assertEquals("http://foo.com/confluence/foo/bar", 
        		UrlUtils.absolutize("/foo/bar", "http://foo.com/confluence/"));
        
    }
    
    @Test
    public void testSanitize() throws MalformedURLException, URISyntaxException {

        try {
        	UrlUtils.sanitize(null);
			fail();
		} catch (MalformedURLException e) {
		}

        try {
        	UrlUtils.sanitize("");
			fail();
		} catch (MalformedURLException e) {
		}

        assertEquals(
            "http://foo.com?bar=%22S%C3%A3o%20Paulo%22", 
            UrlUtils.sanitize("http://foo.com?bar=\"São Paulo\""));

    }
    

    @Test @Ignore
    public void testFollowRedirects() {

        assertEquals(
            "http://fr.gravatar.com/alexdut", 
            UrlUtils.followRedirects("http://www.gravatar.com/e96398d35fcd2cb3df072bcb28c9c917", 10));

    }
}
