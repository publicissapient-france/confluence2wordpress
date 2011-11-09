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
package fr.xebia.confluence2wordpress.transport;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.apache.xmlrpc.DefaultXmlRpcTransportFactory;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcTransport;

/**
 * 
 */
public class EnhancedXmlRpcTransportFactory extends DefaultXmlRpcTransportFactory {

    private Proxy proxy;

    public EnhancedXmlRpcTransportFactory(URL url) {
        super(url);
    }

    public EnhancedXmlRpcTransportFactory(URL url, Proxy proxy) {
        super(url);
        this.proxy = proxy;
    }

    public EnhancedXmlRpcTransportFactory(URL url, String proxyHost, int proxyPort) {
        super(url);
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
    }

    @Override
    public XmlRpcTransport createTransport() throws XmlRpcClientException {
        if ("https".equals(this.url.getProtocol())) {
            //won't handle this for now
            return super.createTransport();
        }
        return new fr.xebia.confluence2wordpress.transport.EnhancedXmlRpcTransport(this.url, this.proxy);
    }

}