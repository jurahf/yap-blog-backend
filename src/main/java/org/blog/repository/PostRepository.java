package org.blog.repository;

import org.blog.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {
    List<Post> getList(String titleSubstring, List<String> requiredTags, int offset, int limit);
    int getCount(String titleSubstring, List<String> requiredTags);
    Optional<Post> getById(long id);
    long create(Post entity);
    long update(long id, Post entity);
    void delete(long id);
}
