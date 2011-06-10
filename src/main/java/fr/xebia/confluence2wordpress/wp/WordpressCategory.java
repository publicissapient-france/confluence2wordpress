package fr.xebia.confluence2wordpress.wp;



public class WordpressCategory {

    private Integer id;

    private Integer parentId;

    private String description;

    private String categoryName;

    private String htmlUrl;

    private String rssUrl;


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public Integer getParentId() {
        return parentId;
    }


    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getCategoryName() {
        return categoryName;
    }


    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public String getHtmlUrl() {
        return htmlUrl;
    }


    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }


    public String getRssUrl() {
        return rssUrl;
    }


    public void setRssUrl(String rssUrl) {
        this.rssUrl = rssUrl;
    }


    @Override
    public String toString() {
        return String.format(
            "WordpressCategory [id=%s, parentId=%s, categoryName=%s, description=%s, htmlUrl=%s, rssUrl=%s]",
            id, parentId, categoryName, description, htmlUrl, rssUrl);
    }


}