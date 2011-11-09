/**
 * Copyright 2011 Alexandre Dutra
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