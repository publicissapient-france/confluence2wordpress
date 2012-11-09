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
package fr.dutra.confluence2wordpress.xmlrpc;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcException;

/**
 * This is an extension to the original XmlRpcClientRequestProcessor from Apache XmlRpc.
 * The main purpose is to get rid of the Base64 multi-threading issue.
 * @see "https://issues.apache.org/jira/browse/CODEC-96"
 */
public class XmlRpcClientRequestProcessor extends org.apache.xmlrpc.XmlRpcClientRequestProcessor {
	
	@Override
	public void encodeRequest(XmlRpcClientRequest request, String encoding, OutputStream out) throws XmlRpcClientException, IOException {
		XmlWriter writer = new XmlWriter(out, encoding);
		try {
			writer.startElement("methodCall");
			writer.startElement("methodName");
			writer.write(request.getMethodName());
			writer.endElement("methodName");
			writer.startElement("params");
	
			int l = request.getParameterCount();
			for (int i = 0; i < l; i++) {
				writer.startElement("param");
				try {
					writer.writeObject(request.getParameter(i));
				} catch (XmlRpcException e) {
					throw new XmlRpcClientException("Failure writing request", e);
				}
				writer.endElement("param");
			}
			writer.endElement("params");
			writer.endElement("methodCall");
			writer.flush();
		} finally {
			writer.close();
		}
	}
}

