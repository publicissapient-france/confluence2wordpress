package fr.xebia.confluence2wordpress.wp.transport;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.apache.xmlrpc.DefaultXmlRpcTransportFactory;
import org.apache.xmlrpc.XmlRpcClientException;
import org.apache.xmlrpc.XmlRpcTransport;

/**
 * 
 */
public class DefaultProxyAwareXmlRpcTransportFactory extends DefaultXmlRpcTransportFactory {

    private Proxy proxy;

    public DefaultProxyAwareXmlRpcTransportFactory(URL url) {
        super(url);
    }

    public DefaultProxyAwareXmlRpcTransportFactory(URL url, Proxy proxy) {
        super(url);
        this.proxy = proxy;
    }

    public DefaultProxyAwareXmlRpcTransportFactory(URL url, String proxyHost, int proxyPort) {
        super(url);
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
    }

    @Override
    public XmlRpcTransport createTransport() throws XmlRpcClientException {
        if ("https".equals(this.url.getProtocol())) {
            //won't handle this for now
            return super.createTransport();
        }
        return new DefaultProxyAwareXmlRpcTransport(this.url, this.proxy);
    }

}