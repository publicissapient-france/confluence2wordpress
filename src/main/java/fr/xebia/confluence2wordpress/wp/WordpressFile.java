package fr.xebia.confluence2wordpress.wp;



public class WordpressFile {

    private String fileName;

    private String url;

    private String mimeType;

    private byte[] data;


    public WordpressFile(String fileName, String mimeType, byte[] data) {
        super();
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.data = data;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public byte[] getData() {
        return data;
    }


    public void setData(byte[] data) {
        this.data = data;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public String getMimeType() {
        return mimeType;
    }


    public void setMimeType(String type) {
        this.mimeType = type;
    }

}