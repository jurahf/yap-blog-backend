package org.blog.repository;

import org.blog.model.PostImage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JdbcNativePostImageRepository implements PostImageRepository {
    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostImageRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<PostImage> getByPostId(long postId) {
        List<PostImage> images = jdbcTemplate.query(
                "select p.postId, p.path " +
                        "from postImages p " +
                        "WHERE p.postId = ? ",
                (rs, rowNum) -> {
                    return new PostImage(
                        rs.getLong("postId"),
                        rs.getString("path")
                    );
                },
                postId
        );

        return images.stream().findFirst();
    }

    @Override
    public long createOrUpdate(long postId, String path) {
        jdbcTemplate.update("delete from postImages where postId = ?", postId);
        jdbcTemplate.update("insert into postImages (postId, path) values (?, ?)", postId, path);

        return postId;
    }
}
