package org.blog.repository;

import org.blog.model.Comment;
import org.blog.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcNativeCommentRepository implements CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativeCommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> getList(long postId) {
        return jdbcTemplate.query(
                "select c.id, c.text, c.postId " +
                        "from comments c " +
                        "WHERE " +
                        "c.postId = ?",
                (rs, rowNum) -> {
                    return new Comment(
                            rs.getLong("id"),
                            rs.getString("text"),
                            rs.getLong("postId")
                    );
                },
                postId
        );
    }

    @Override
    public Optional<Comment> getById(long postId, long id) {
        List<Comment> comments = jdbcTemplate.query(
                "select c.id, c.text, c.postId " +
                        "from comments c " +
                        "WHERE c.id = ? " +
                        "AND c.postId = ?",
                (rs, rowNum) -> {
                    return new Comment(
                            rs.getLong("id"),
                            rs.getString("text"),
                            rs.getLong("postId")
                    );
                },
                id, postId
        );

        return comments.stream().findFirst();
    }

    @Override
    public long create(Comment entity) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("comments")
                .usingGeneratedKeyColumns("id");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("postId", entity.getPostId());
        parameters.put("text", entity.getText());

        Number id = insert.executeAndReturnKey(parameters);
        return id.longValue();
    }

    @Override
    public long update(long postId, long id, Comment entity) {
        int rowsAffected =
                jdbcTemplate.update("update comments set text = ? where id = ? AND postId = ?",
                        entity.getText(),
                        id,
                        postId);

        return id;
    }

    @Override
    public void delete(long postId, long id) {
        jdbcTemplate.update("delete from comments where id = ? AND postId = ?", id, postId);
    }
}
