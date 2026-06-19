package org.blog.service;

import org.blog.dtos.*;
import org.blog.model.Post;
import org.blog.repository.PostRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post samplePost1;
    private Post samplePost2;
    private PostCreateRequestDto createRequest;
    private PostUpdateRequestDto updateRequest;

    @BeforeEach
    void setUp() {
        samplePost1 = new Post(1, "Пост 1", "Пример поста 1",
                List.of("post", "test"), 5, 2);
        samplePost2 = new Post(2, "Пост 2", "Пример поста 2",
                List.of("post", "it"), 3, 1);
    }

    @Test
    @DisplayName("getList - делается эллипсис на 128 символов")
    void getList_ShouldTruncateLongText() {
        // given
        String longText = "a".repeat(200);
        Post postWithLongText = new Post(3, "Long Post", longText, List.of(), 0, 0);
        when(postRepository.getList(null, List.of(), 0, 10)).thenReturn(List.of(postWithLongText));

        // when
        PostListDto result = postService.getList(null, 1, 10);

        // then
        assertThat(result.getPosts().get(0).getText()).hasSize(131); // 128 + "..."
        assertThat(result.getPosts().get(0).getText()).endsWith("...");
    }

    @Test
    @DisplayName("getList - старница меньше 1")
    void getList_WithInvalidPageNumber_ShouldSetToFirstPage() {
        // given
        List<Post> allPosts = List.of(samplePost1, samplePost2);
        when(postRepository.getList(null, List.of(), 0, 1)).thenReturn(allPosts);

        // when
        PostListDto result = postService.getList(null, 0, 1);

        // then
        assertThat(result.getPosts()).hasSize(2);
        assertThat(result.isHasPrev()).isFalse();
    }

    @Test
    @DisplayName("getById - поиск поста по id")
    void getById_WhenPostExists_ShouldReturnPost() {
        // given
        when(postRepository.getById(1L)).thenReturn(Optional.of(samplePost1));

        // when
        Optional<PostDto> result = postService.getById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getTitle()).isEqualTo("Пост 1");
        assertThat(result.get().getText()).isEqualTo("Пример поста 1");
        verify(postRepository, times(1)).getById(1L);
    }

    @Test
    @DisplayName("getById - поиск поста по id, когда такого id нет")
    void getById_WhenPostNotExists_ShouldReturnEmpty() {
        // given
        when(postRepository.getById(99L)).thenReturn(Optional.empty());

        // when
        Optional<PostDto> result = postService.getById(99L);

        // then
        assertThat(result).isEmpty();
        verify(postRepository, times(1)).getById(99L);
    }

    @Test
    @DisplayName("update - обнволение несуществующего - ошибка")
    void update_WhenPostNotExists_ShouldThrowException() {
        // given
        when(postRepository.getById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.update(99L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class);

        verify(postRepository, never()).update(anyLong(), any());
    }

    @Test
    @DisplayName("delete - удаление поста")
    void delete_ShouldCallRepository() {
        // given
        doNothing().when(postRepository).delete(1L);

        // when
        postService.delete(1L);

        // then
        verify(postRepository, times(1)).delete(1L);
    }

    @Test
    @DisplayName("incLikes - инеремент лайков")
    void incLikes_WhenPostExists_ShouldIncrementAndReturnNewCount() {
        // given
        Post postWithLikes = new Post(1, "Title", "Text", List.of(), 5, 0);
        when(postRepository.getById(1L)).thenReturn(Optional.of(postWithLikes));
        when(postRepository.update(eq(1L), any(Post.class))).thenReturn(1L);
        when(postRepository.getById(1L)).thenReturn(Optional.of(postWithLikes));

        // when
        int newLikesCount = postService.incLikes(1L);

        // then
        assertThat(newLikesCount).isEqualTo(6);
        verify(postRepository, times(2)).getById(1L);
        verify(postRepository, times(1)).update(eq(1L), any(Post.class));
    }

}
