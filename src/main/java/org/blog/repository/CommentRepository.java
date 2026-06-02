package org.blog.repository;

import org.blog.model.Comment;
import org.blog.model.Post;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> getList(long postId);
    Optional<Comment> getById(long postId, long id);
    long create(Comment entity);
    long update(long postId, long id, Comment entity);
    void delete(long postId, long id);
}
