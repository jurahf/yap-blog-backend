package org.blog.repository;

import org.blog.dtos.PostUpdateRequestDto;
import org.blog.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> getList() {
        // Выполняем запрос с помощью JdbcTemplate
        // Преобразовываем ответ с помощью RowMapper
        return jdbcTemplate.query(
                "select p.id, p.title, p.text, COALESCE(p.tags, '') as tags, p.likesCount, COUNT(c.id) AS commentsCount from posts p " +
                        "LEFT JOIN comments c ON c.postId = p.id " +
                        "GROUP BY p.id " +
                        "ORDER BY p.id ",
                (rs, rowNum) -> {
                    String tagsStr = rs.getString("tags");
                    List<String> tagsList = tagsStr != null ? List.of(tagsStr.split(",")) : List.of();

                    return new Post(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("text"),
                            tagsList,
                            rs.getInt("likesCount"),
                            rs.getInt("commentsCount")
                    );
                });
    }

    @Override
    public Optional<Post> getById(long id) {
        List<Post> posts = jdbcTemplate.query(
                "select p.id, p.title, p.text, p.tags, p.likesCount, COUNT(c.id) AS commentsCount " +
                        "from posts p " +
                        "LEFT JOIN comments c ON c.postId = p.id " +
                        "WHERE p.id = ? " +
                        "GROUP BY p.id",
                (rs, rowNum) -> {
                    String tagsStr = rs.getString("tags");
                    List<String> tagsList = tagsStr != null ? List.of(tagsStr.split(",")) : List.of();
                    return new Post(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("text"),
                            tagsList,
                            rs.getInt("likesCount"),
                            rs.getInt("commentsCount")
                    );
                },
                id
        );

        return posts.stream().findFirst();
    }

    @Override
    public long create(Post post) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("posts")
                .usingGeneratedKeyColumns("id");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("title", post.getTitle());
        parameters.put("text", post.getText());
        parameters.put("tags", String.join(",", post.getTags()));
        parameters.put("likesCount", 0);

        Number id = insert.executeAndReturnKey(parameters);
        return id.longValue();
    }

    @Override
    public long update(long id, Post post) {
        int rowsAffected =
                jdbcTemplate.update("update posts set title = ?, text = ?, tags = ? where id = ?",
                        post.getTitle(),
                        post.getText(),
                        String.join(",", post.getTags()),
                        id);

        return id;
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("delete from posts where id = ?", id);
    }

}
