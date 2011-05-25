package fr.dutra.xebia.wiki2html.wp;

import java.util.Arrays;
import java.util.Date;


public class WordPressPost {

    private Integer postId;

    private Date dateCreated;

    private boolean draft = true;

    private String title;

    private String postSlug;

    private String body;

    private Integer authorId;

    private String[] categoryNames;

    private int[] categoryIds;

    private String[] tagNames;



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


    public String[] getCategoryNames() {
        return categoryNames;
    }


    public void setCategoryNames(String... categoryNames) {
        this.categoryNames = categoryNames;
    }


    public String[] getTagNames() {
        return tagNames;
    }


    public void setTagNames(String... tagNames) {
        this.tagNames = tagNames;
    }


    public int[] getCategoryIds() {
        return categoryIds;
    }


    public void setCategoryIds(int... categoryIds) {
        this.categoryIds = categoryIds;
    }



    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WordPressPost [authorId=").append(authorId).append(", body=").append(body).append(", categoryNames=").append(Arrays.toString(categoryNames)).append(
        ", dateCreated=").append(dateCreated).append(", draft=").append(draft).append(", postId=").append(postId).append(", postSlug=").append(postSlug).append(", tagNames=")
        .append(Arrays.toString(tagNames)).append(", title=").append(title).append("]");
        return builder.toString();
    }


}