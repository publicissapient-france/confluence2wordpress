package fr.dutra.confluence2wordpress.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.Test;

import fr.dutra.confluence2wordpress.util.UrlUtils;



public class UrlUtilsTest {

    @Test
    public void test() throws MalformedURLException, URISyntaxException {

        Assert.assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "/confluence/download/attachments/983042/image.png?version=1&amp;modificationDate=1327402370523", 
                "http://localhost/confluence"));

        Assert.assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "http://localhost:1990/confluence/download/attachments/983042/image.png", 
                "http://localhost:1990/confluence"));

        Assert.assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "/confluence/download/attachments/983042/image.png?version=1&amp;modificationDate=1327402370523", 
                "http://localhost/confluence/"));

        Assert.assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "http://localhost:1990/confluence/download/attachments/983042/image.png", 
                "http://localhost:1990/confluence/"));

        Assert.assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "/download/attachments/983042/image.png?version=1&amp;modificationDate=1327402370523", 
                "http://localhost"));

        Assert.assertEquals(
            "/download/attachments/983042/image.png", 
            UrlUtils.extractConfluenceRelativePath(
                "http://localhost:1990/download/attachments/983042/image.png", 
                "http://localhost:1990"));


    }
}
