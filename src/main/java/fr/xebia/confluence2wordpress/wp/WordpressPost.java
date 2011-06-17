package fr.xebia.confluence2wordpress.wp;

import java.util.Date;
import java.util.List;


public class WordpressPost {

    private Integer postId;

    private Date dateCreated;

    private boolean draft = true;

    private String title;

    private String postSlug;

    private String body;

    private Integer authorId;

    private List<String> categoryNames;

    private List<String> tagNames;

    private List<Integer> categoryIds;


    public Integer getPostId() {
        return postId;
    }



    public void setPostId(Integer postId) {
        this.postId = postId;
    }



    public Date getDateCreated() {
        return dateCreated;
    }



    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    public boolean isDraft() {
        return draft;
    }


    public void setDraft(boolean draft) {
        this.draft = draft;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getPostSlug() {
        return postSlug;
    }


    public void setPostSlug(String postSlug) {
        this.postSlug = postSlug;
    }


    public String getBody() {
        return body;
    }


    public void setBody(String body) {
        this.body = body;
    }


    public Integer getAuthorId() {
        return authorId;
    }


    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }


    public List<String> getCategoryNames() {
        return categoryNames;
    }


    public void setCategoryNames(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }


    public List<String> getTagNames() {
        return tagNames;
    }


    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }


    public List<Integer> getCategoryIds() {
        return categoryIds;
    }


    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }


}