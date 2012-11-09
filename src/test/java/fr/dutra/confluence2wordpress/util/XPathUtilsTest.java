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

import org.junit.Assert;
import org.junit.Test;

import fr.dutra.confluence2wordpress.util.XPathUtils;


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
