package org.blog.repository;

import org.blog.model.PostImage;

import java.util.Optional;

public interface PostImageRepository {
    Optional<PostImage> getByPostId(long postId);
    long createOrUpdate(long postId, String path);
}
