package org.blog.repository;

import org.blog.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Post> getList(String titleSubstring, List<String> requiredTags, int offset, int limit) {
        // Выполняем запрос с помощью JdbcTemplate
        // Преобразовываем ответ с помощью RowMapper
        List<Object> params = new ArrayList<>();
        String where = getWhereClauseSql(titleSubstring, requiredTags, Optional.of(offset), Optional.of(limit), true, params);

        return jdbcTemplate.query(
                "select p.id, p.title, p.text, COALESCE(p.tags, '') as tags, p.likesCount, COUNT(c.id) AS commentsCount from posts p " +
                        "LEFT JOIN comments c ON c.postId = p.id " + where,
                (rs, rowNum) -> {
                    String tagsStr = rs.getString("tags");
                    List<String> tagsList = tagsStr != null && !tagsStr.isEmpty()
                            ? List.of(tagsStr.split(","))
                            : List.of();

                    return new Post(
                            rs.getLong("id"),
                            rs.getString("title"),
                            rs.getString("text"),
                            tagsList,
                            rs.getInt("likesCount"),
                            rs.getInt("commentsCount")
                    );
                },
                params.toArray()
        );
    }

    @Override
    public int getCount(String titleSubstring, List<String> requiredTags) {
        List<Object> params = new ArrayList<>();
        String where = getWhereClauseSql(titleSubstring, requiredTags, Optional.empty(), Optional.empty(), false, params);

        return jdbcTemplate.queryForObject(
                "select count(distinct p.id) from posts p " + where,
                params.toArray(),
                Integer.class
        );
    }

    private String getWhereClauseSql(
            String titleSubstring,
            List<String> requiredTags,
            Optional<Integer> offset,
            Optional<Integer> limit,
            boolean oredering,
            List<Object> params) {
        StringBuilder sql = new StringBuilder();

        List<String> conditions = new ArrayList<>();

        // Фильтрация по title
        if (titleSubstring != null && !titleSubstring.trim().isEmpty()) {
            conditions.add("LOWER(p.title) LIKE LOWER ( ? )");
            params.add("%" + titleSubstring + "%");
        }

        // Фильтрация по тегам
        if (requiredTags != null && !requiredTags.isEmpty()) {
            for (String tag : requiredTags) {
                // Проверяем, что тег содержится в строке tags
                conditions.add("LOWER(CONCAT(',', COALESCE(p.tags, ''), ',')) LIKE LOWER( ? )");
                params.add("%," + tag + ",%");
            }
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" AND ", conditions));
        }

        if (oredering) {
            sql.append(" GROUP BY p.id ORDER BY p.id ");
        }

        // пагинация
        if (limit.isPresent() && offset.isPresent()) {
            sql.append(" LIMIT ? OFFSET ?");
            params.add(limit.get());
            params.add(offset.get());
        }

        return sql.toString();
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
                jdbcTemplate.update("update posts set title = ?, text = ?, tags = ?, likesCount = ? where id = ?",
                        post.getTitle(),
                        post.getText(),
                        String.join(",", post.getTags()),
                        post.getLikesCount(),
                        id);

        return id;
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("delete from posts where id = ?", id);
    }

}
