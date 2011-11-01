package fr.xebia.confluence2wordpress.transport;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URL;

import org.apache.xmlrpc.DefaultXmlRpcTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.xebia.confluence2wordpress.util.XmlRpcUtils;

public class EnhancedXmlRpcTransport extends DefaultXmlRpcTransport {

    private static final Logger LOG = LoggerFactory.getLogger(EnhancedXmlRpcTransport.class);
    
    private Proxy proxy;

    public EnhancedXmlRpcTransport(URL url, Proxy proxy) {
        super(url);
        this.proxy = proxy;
    }

    @Override
    public InputStream sendXmlRpc(byte[] request) throws IOException {
        if(LOG.isDebugEnabled()){
            XmlRpcUtils.logRequest(LOG, request);
        }
        if(proxy == null) {
            //return super.sendXmlRpc(request);
            this.con = this.url.openConnection();
        } else {
            this.con = this.url.openConnection(proxy);
        }
        this.con.setDoInput(true);
        this.con.setDoOutput(true);
        this.con.setUseCaches(false);
        this.con.setAllowUserInteraction(false);
        this.con.setRequestProperty("Content-Length", Integer.toString(request.length));
        this.con.setRequestProperty("Content-Type", "text/xml");
        if (this.auth != null) {
            this.con.setRequestProperty("Authorization", "Basic " + this.auth);
        }
        OutputStream out = this.con.getOutputStream();
        out.write(request);
        out.flush();
        out.close();
        InputStream response = this.con.getInputStream();
        if(LOG.isDebugEnabled()){
            response = XmlRpcUtils.logResponse(LOG, response);
        }
        return response;
    }
}
