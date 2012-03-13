package fr.dutra.confluence2wordpress.util;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.dutra.confluence2wordpress.util.CodecUtils;

public class CodecUtilsTest {

    private String text = "azertyuiopqsdfghjklmù&é\"'(-è_çà)=";

	@Test
    public void test() throws IOException {
		String encoded = CodecUtils.compressAndEncode(text);
		String actual = CodecUtils.decodeAndExpand(encoded);
		Assert.assertEquals(text, actual);
    }
}