package fr.xebia.confluence2wordpress.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class XmlRpcUtils {

    public static void logRequest(Logger logger, byte[] request) {
        logger.debug("---- Request ----");
        logger.debug(XmlUtils.toPrettyXml(new String(request)));
    }

    public static InputStream logResponse(Logger logger, InputStream istream)
        throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(istream));
            String line = null;
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            while ((line = reader.readLine()) != null) {
                pw.println(line);
            }
            pw.flush();
            pw.close();
            String response = sw.toString();
            logger.debug("---- Response ----");
            logger.debug(XmlUtils.toPrettyXml(response));
            return new ByteArrayInputStream(response.getBytes());
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

}