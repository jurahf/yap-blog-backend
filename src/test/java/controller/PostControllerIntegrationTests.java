package controller;


import org.blog.WebConfiguration;
import org.blog.configuration.DataSourceConfiguration;
import org.blog.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringJUnitConfig(classes = {
        DataSourceConfiguration.class,
        WebConfiguration.class,
})
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostControllerIntegrationTests {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostService service;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        // Чистим и наполняем БД перед каждым тестом
        jdbcTemplate.execute("DELETE FROM POSTS;");
        jdbcTemplate.execute("insert into posts(id, title, text, tags, likesCount) values (1, 'Пост 1', 'Первый пост про localhost', 'it,web', 2);");
        jdbcTemplate.execute("insert into posts(id, title, text, tags, likesCount) values (2, 'Пост 2', 'Пост про IT', 'it', 0);");
        jdbcTemplate.execute("insert into posts(id, title, text, tags, likesCount) values (3, 'Пост 3', 'Пост на отвлеченные темы', null, 0);");

        jdbcTemplate.execute("insert into comments(postId, text) values (1, 'Это твой первый длиннопост?');");
        jdbcTemplate.execute("insert into comments(postId, text) values (1, 'Поздравляю с открытием блога!');");
        jdbcTemplate.execute("insert into comments(postId, text) values (3, 'Наконец-то нормальная тема!');");
    }


    @Test
    void getPostsPagination() throws Exception {
        mockMvc.perform(get("/api/posts?search=пост&pageNumber=1&pageSize=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts", hasSize(2)))
                .andExpect(jsonPath("$.posts[0].title").value("Пост 1"))
                .andExpect(jsonPath("$.posts[1].title").value("Пост 2"));
    }


    @Test
    void getPostsAll() throws Exception {
        mockMvc.perform(get("/api/posts?search=пост&pageNumber=1&pageSize=100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts", hasSize(3)));
    }

    @Test
    void createPost() throws Exception {
        String json = """
                  {
                    "title": "Название поста 4",
                    "text": "Текст поста в формате Markdown...",
                    "tags": ["tag_1", "tag_2"]
                  }
                """;

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Название поста 4"));

        mockMvc.perform(get("/api/posts?search=пост&pageNumber=1&pageSize=100"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts", hasSize(4)));
    }

    @Test
    void updatePost() throws Exception {
        String json = """
                  {
                    "id": 1,
                    "title": "Обновленное название",
                    "text": "Текст поста в формате Markdown...",
                    "tags": ["tag_1", "tag_2"]
                  }
                """;

        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Обновленное название"));
    }

    @Test
    void getComments() throws Exception {
        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void createComments() throws Exception {
        String json = """
                  {
                    "text": "Комментарий к посту",
                    "postId": 1
                  }
                """;

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Комментарий к посту"));

        mockMvc.perform(get("/api/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)));
    }

}
