package fr.xebia.confluence2wordpress.wp;


public class WordpressXmlRpcException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -1502068432250743086L;

    public WordpressXmlRpcException(String message) {
        super(message);
    }

    public WordpressXmlRpcException(String message, Throwable cause) {
        super(message, cause);
    }

}
