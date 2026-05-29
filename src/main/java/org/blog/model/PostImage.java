package org.blog.model;

/// Картинка поста - модель хранения. 1 к 1 с постом, но выносим в отдельный класс, может быть захотим хранить отдельно
public class PostImage {

    private long postId;
    private String path;

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
