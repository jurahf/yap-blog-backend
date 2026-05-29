package org.blog.repository;

import org.blog.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public Post create(Post user) {
//        // Формируем insert-запрос с параметрами
//        jdbcTemplate.update("insert into users(first_name, last_name, age, active) values(?, ?, ?, ?)",
//                user.getFirstName(), user.getLastName(), user.getAge(), user.isActive());

        return null;
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("delete from posts where id = ?", id);
    }

    @Override
    public Post update(long id, Post user) {
//        jdbcTemplate.update("update users set first_name = ?, last_name = ?, age = ?, active = ? where id = ?",
//                user.getFirstName(), user.getLastName(), user.getAge(), user.isActive(), id);
        return null;
    }
}
