package fr.xebia.confluence2wordpress.xmlrpc;

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
	}

}

