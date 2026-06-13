package org.blog.dtos;

import java.util.List;

public class PostUpdateRequestDto {

    private long id;
    private String title;
    private String text;
    private List<String> tags;

    public  PostUpdateRequestDto() {

    }

    public PostUpdateRequestDto(long id, String title, String text, List<String> tags) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
