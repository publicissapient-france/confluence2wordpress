package fr.xebia.confluence2wordpress.util;

import org.junit.Assert;
import org.junit.Test;


public class XPathUtilsTest {

	@Test
    public void test() throws Exception {
    	int count = XPathUtils.evaluateXPathAsInt(
    			"<div class=\"error\">boo</div>", 
    			"count(/*[name() != 'div' or @class != 'error'])");
    	Assert.assertEquals(0, count);
    	String msg = XPathUtils.evaluateXPathAsString(
    			"<div class=\"error\">boo</div>", 
    			"/div[@class = 'error']");
    	Assert.assertEquals("boo", msg);
	}
}
