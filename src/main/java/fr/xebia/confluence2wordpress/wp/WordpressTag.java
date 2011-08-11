package fr.xebia.confluence2wordpress.wp;



public class WordpressTag {

    private Integer id;

    private String name;

    private Integer count;

    private String slug;

    private String htmlUrl;

    private String rssUrl;

    public WordpressTag() {
        super();
    }

    public WordpressTag(String name) {
        super();
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
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
            "WordpressTag [id=%s, name=%s, count=%s, htmlUrl=%s, rssUrl=%s, slug=%s]",
            id, name, count, htmlUrl, rssUrl, slug);
    }


}