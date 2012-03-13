package fr.dutra.confluence2wordpress.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class CodecUtils {

	private static final String REGEX = "\\A";
	
	private static final String UTF_8 = "UTF-8";

	public static String compressAndEncode(String text) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzos = new GZIPOutputStream(new Base64OutputStream(baos, true, 0, null));
		try {
			gzos.write(text.getBytes(UTF_8));
		} finally {
			gzos.close();
		}
		return new String(baos.toByteArray(), UTF_8);
	}

	public static String decodeAndExpand(String encoded) throws IOException {
		GZIPInputStream gzis = 
			new GZIPInputStream(
				new Base64InputStream(
					new ByteArrayInputStream(
						encoded.getBytes(UTF_8)), false, 0, null));
		try {
			String decoded = new Scanner(gzis, UTF_8).useDelimiter(REGEX).next();
			return decoded;
		} finally {
			gzis.close();
		}
	}
}