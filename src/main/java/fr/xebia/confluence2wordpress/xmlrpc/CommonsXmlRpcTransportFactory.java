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
package fr.xebia.confluence2wordpress.xmlrpc;

import java.net.URL;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.xmlrpc.CommonsXmlRpcTransport;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcTransport;
import org.apache.xmlrpc.XmlRpcTransportFactory;

public class CommonsXmlRpcTransportFactory implements XmlRpcTransportFactory {

	private final HttpClient client;
	
	private final URL url;

	public CommonsXmlRpcTransportFactory(URL url, int maxConnections) {
		this(url, null, -1, maxConnections);
	}

	public CommonsXmlRpcTransportFactory(URL url, String proxyHost, int proxyPort, int maxConnections) {
		this.url = url;
		HostConfiguration hostConf = new HostConfiguration();
		hostConf.setHost(url.getHost(), url.getPort());
		if (proxyHost != null && proxyPort != -1) {
			hostConf.setProxy(proxyHost, proxyPort);
		}
		HttpConnectionManagerParams connParam = new HttpConnectionManagerParams();
		connParam.setMaxConnectionsPerHost(hostConf, maxConnections);
		connParam.setMaxTotalConnections(maxConnections);
		MultiThreadedHttpConnectionManager conMgr = new MultiThreadedHttpConnectionManager();
		conMgr.setParams(connParam);
		client = new HttpClient(conMgr);
		client.setHostConfiguration(hostConf);
	}

	@Override
	public XmlRpcTransport createTransport() throws XmlRpcClientException {
		return new CommonsXmlRpcTransport(url, client);
	}

	@Override
	public void setProperty(String arg0, Object arg1) {
		throw new UnsupportedOperationException();
	}

}