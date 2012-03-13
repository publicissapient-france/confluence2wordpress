/**
 * Copyright 2011 Alexandre Dutra
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package fr.dutra.confluence2wordpress.wp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;


public class WordpressPost implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer postId;

    private Date dateCreated;

    private boolean draft = true;

    private String title;

    private String postSlug;

    private String body;

    private Integer authorId;

    private List<String> categoryNames;

    private List<String> tagNames;

    private String link;

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDigest() {
        return DigestUtils.sha256Hex(toString());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WordpressPost [");
        if (postId != null) {
            builder.append("postId=");
            builder.append(postId);
            builder.append(", ");
        }
        if (dateCreated != null) {
            builder.append("dateCreated=");
            builder.append(dateCreated);
            builder.append(", ");
        }
        builder.append("draft=");
        builder.append(draft);
        builder.append(", ");
        if (title != null) {
            builder.append("title=");
            builder.append(title);
            builder.append(", ");
        }
        if (postSlug != null) {
            builder.append("postSlug=");
            builder.append(postSlug);
            builder.append(", ");
        }
        if (body != null) {
            builder.append("body=");
            builder.append(body);
            builder.append(", ");
        }
        if (authorId != null) {
            builder.append("authorId=");
            builder.append(authorId);
            builder.append(", ");
        }
        if (categoryNames != null) {
            builder.append("categoryNames=");
            builder.append(categoryNames);
            builder.append(", ");
        }
        if (tagNames != null) {
            builder.append("tagNames=");
            builder.append(tagNames);
            builder.append(", ");
        }
        if (link != null) {
            builder.append("link=");
            builder.append(link);
        }
        builder.append("]");
        return builder.toString();
    }

}