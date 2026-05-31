package org.blog.dtos;

public class CommentCreateRequestDto {
    private String text;
    private long postId;

    public CommentCreateRequestDto() {

    }

    public CommentCreateRequestDto(String text, long postId) {
        this.text = text;
        this.postId = postId;
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
