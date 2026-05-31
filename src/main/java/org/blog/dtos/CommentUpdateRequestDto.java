package org.blog.dtos;

public class CommentUpdateRequestDto {
    private long id;
    private String text;
    private long postId;

    public CommentUpdateRequestDto() {
    }

    public CommentUpdateRequestDto(long id, String text, long postId) {
        this.id = id;
        this.text = text;
        this.postId = postId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }
}
