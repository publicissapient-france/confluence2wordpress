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
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.dutra.confluence2wordpress.util.CodecUtils;

public class CodecUtilsTest {

    private String text = "azertyuiopqsdfghjklmù&é\"'(-è_çà)=";

    private String base64Legacy = "H4sIAAAAAAAAAD2Q30/CMBCA/xVyDz5twAbTsIQYgzH6oBLkwcT4UNZbaeza5XYFlfC/2w6kD23S+/HdfQdoXcdPEsosL8bXCUgSNUPJ5DHpY2/GKyiBcOcxlZi2hF2H6TdutEjzcZan0wISEJ63jk6NEqgEo3L08yIa7KD8gFUsH0gcnMrhMwEW6hy23pgAEwrXmg0G2iV92acP3iMtULSyjlAunK2NR1vhs6jI9QTWbUywtQuPdYzh2Quy2qoz7Y6Z9MZzRB6OCbiWdaN/8cHR6n75v7O2lfES168LKGthuqgBqRFG268w2Za5LUejjXFq2DsY1jS6bee9vquw3U7jfh5bBb7UCrtgE9KiyLLZzWSaxd8gZ0EY7mhrUuTZbJJPx+EEjcyi2jZo+eKldoHOj9yY04jHP0n/VJ20AQAA";
    
    private String decoded = "{\"postId\":12506,\"draft\":true,\"postSlug\":\"revue-de-presse-xebia-2012-45\",\"authorId\":12,\"categoryNames\":[\"Revue de presse\"],\"tagNames\":null,\"pageTitle\":\"Revue de Presse Xebia\",\"ignoredConfluenceMacros\":[\"tip\",\"info\",\"note\",\"warning\"],\"tagAttributes\":{},\"optimizeForRDP\":true,\"includeTOC\":false,\"permalink\":\"http://blog.xebia.fr/?p=12506&preview=true\",\"digest\":\"-551197341\",\"dateCreated\":1352193240000,\"attachments\":null,\"formatHtml\":true}";
	
    @Test
    public void test_utf8() throws IOException {
		String encoded = CodecUtils.compressAndEncode(text);
		String actual = CodecUtils.decodeAndExpand(encoded);
		Assert.assertEquals(text, actual);
    }
	

	@Test
    public void test_should_decode() throws IOException {
		String encoded = CodecUtils.compressAndEncode(decoded);
		String actual = CodecUtils.decodeAndExpand(encoded);
		Assert.assertEquals(decoded, actual);
    }

	@Test
    public void test_should_decode_legacy() throws IOException {
		String actual = CodecUtils.decodeAndExpand(base64Legacy);
		Assert.assertEquals(decoded, actual);
    }
}