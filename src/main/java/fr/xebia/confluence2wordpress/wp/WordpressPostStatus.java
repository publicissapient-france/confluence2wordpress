package fr.xebia.confluence2wordpress.wp;



public enum WordpressPostStatus {

    DRAFT   ("draft"  , false),
    PENDING ("pending", false),
    PRIVATE ("private", false),
    PUBLISH ("publish", true);
    
    private final String code;

    private final boolean publish;
    
    private WordpressPostStatus(String code, boolean publish) {
        this.code = code;
        this.publish = publish;
    }

    public String getCode() {
        return code;
    }

    public boolean isPublish() {
        return publish;
    }
    
    
}