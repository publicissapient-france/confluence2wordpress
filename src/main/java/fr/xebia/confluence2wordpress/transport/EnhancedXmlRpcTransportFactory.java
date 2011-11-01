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