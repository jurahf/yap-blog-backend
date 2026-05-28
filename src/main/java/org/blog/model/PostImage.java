package org.blog.model;

/// Картинка поста - модель хранения. 1 к 1 с постом, но выносим в отдельный класс, может быть захотим хранить отдельно
public class PostImage {

    private long postId;
    private byte[] content;

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
