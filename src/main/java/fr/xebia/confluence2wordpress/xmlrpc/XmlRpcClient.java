package fr.xebia.confluence2wordpress.xmlrpc;

import java.io.IOException;
import java.net.URL;
import java.util.EmptyStackException;

import org.apache.xmlrpc.XmlRpcClientRequest;
import org.apache.xmlrpc.XmlRpcClientResponseProcessor;
import org.apache.xmlrpc.XmlRpcClientWorker;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcTransport;
import org.apache.xmlrpc.XmlRpcTransportFactory;

/**
 * This is an extension to the original XmlRpcClient from Apache XmlRpc.
 * The main purpose is to get rid of the Base64 multi-threading issue.
 * @see "https://issues.apache.org/jira/browse/CODEC-96"
 */
public class XmlRpcClient extends org.apache.xmlrpc.XmlRpcClient {

	public XmlRpcClient(URL url, XmlRpcTransportFactory transportFactory) {
		super(url, transportFactory);
	}

	@Override
	public Object execute(XmlRpcClientRequest request, XmlRpcTransport transport) throws XmlRpcException, IOException {
		XmlRpcClientWorker worker = getWorker();
		try {
			return worker.execute(request, transport);
		} finally {
			releaseWorker(worker);
		}
	}

	private synchronized XmlRpcClientWorker getWorker() throws IOException {
		try {
			XmlRpcClientWorker w = (XmlRpcClientWorker) pool.pop();
			workers += 1;
			return w;
		} catch (EmptyStackException x) {
			if (workers < getMaxThreads()) {
				workers += 1;
				return new XmlRpcClientWorker(new XmlRpcClientRequestProcessor(), new XmlRpcClientResponseProcessor());
			}
			throw new IOException("XML-RPC System overload");
		}
	}

	@SuppressWarnings("unchecked")
	private synchronized void releaseWorker(XmlRpcClientWorker w) {
		if (pool.size() < getMaxThreads()) {
			pool.push(w);
		}
		workers -= 1;
	}

}
