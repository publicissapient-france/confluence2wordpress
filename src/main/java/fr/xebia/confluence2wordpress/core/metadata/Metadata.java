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
package fr.xebia.confluence2wordpress.core.metadata;

import java.io.Serializable;
import java.util.List;

import fr.xebia.confluence2wordpress.wp.WordpressPost;

/**
 * @author Alexandre Dutra
 *
 */
public class Metadata implements Serializable {

    private static final long serialVersionUID = 1L;

    @MetadataItem("Post ID")
    private Integer postId;
    
    @MetadataItem("Is Draft")
    private boolean draft = true;

    @MetadataItem("Post Slug")
    private String postSlug;

    @MetadataItem("Author ID")
    private Integer authorId;

    @MetadataItem("Categories")
    private List<String> categoryNames;

    @MetadataItem("Tags")
    private List<String> tagNames;

    @MetadataItem("Title")
    private String pageTitle;

    @MetadataItem("Ignore Confluence Macros")
    private List<String> ignoreConfluenceMacros;

    @MetadataItem("Optimize for Press Review")
    private boolean optimizeForRDP;

    @MetadataItem("Include TOC")
    private boolean includeTOC;
    
    @MetadataItem("Permalink")    
    private String permalink;

    @MetadataItem("Digest")    
    private String digest;

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public List<String> getIgnoreConfluenceMacros() {
        return ignoreConfluenceMacros;
    }

    public void setIgnoreConfluenceMacros(List<String> ignoreConfluenceMacros) {
        this.ignoreConfluenceMacros = ignoreConfluenceMacros;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getPostSlug() {
        return postSlug;
    }

    public void setPostSlug(String postSlug) {
        this.postSlug = postSlug;
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

    
    public boolean isDraft() {
        return draft;
    }

    
    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    
    public boolean isOptimizeForRDP() {
        return optimizeForRDP;
    }

    
    public void setOptimizeForRDP(boolean optimizeForRDP) {
        this.optimizeForRDP = optimizeForRDP;
    }

    
    public String getPermalink() {
        return permalink;
    }

    
    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    
    public boolean isIncludeTOC() {
        return includeTOC;
    }

    
    public void setIncludeTOC(boolean includeTOC) {
        this.includeTOC = includeTOC;
    }
    
    
    public String getDigest() {
        return digest;
    }

    
    public void setDigest(String digest) {
        this.digest = digest;
    }

    public WordpressPost createPost(String body){
        WordpressPost post = new WordpressPost();
        post.setDraft(this.isDraft());
        post.setPostId(this.getPostId());
        post.setAuthorId(this.getAuthorId());
        post.setTitle(this.getPageTitle());
        post.setBody(body);
        post.setPostSlug(this.getPostSlug());
        if(this.getCategoryNames()!=null){
            post.setCategoryNames(this.getCategoryNames()); //categories must exist.
        }
        if(this.getTagNames()!= null){
            post.setTagNames(this.getTagNames()); //tags are dynamically created.
        }
        return post;
    }
    
    public void updateFromPost(WordpressPost post) {
        this.setPostId(post.getPostId());
        this.setDraft(post.isDraft());
        this.setPageTitle(post.getTitle());
        this.setPostSlug(post.getPostSlug());
        this.setAuthorId(post.getAuthorId());
        this.setCategoryNames(post.getCategoryNames());
        this.setTagNames(post.getTagNames());
        this.setPermalink(post.getLink());
        this.setDigest(post.getDigest());
    }


}