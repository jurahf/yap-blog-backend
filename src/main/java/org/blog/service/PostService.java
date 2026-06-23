package org.blog.service;

import org.blog.dtos.PostCreateRequestDto;
import org.blog.dtos.PostDto;
import org.blog.dtos.PostListDto;
import org.blog.dtos.PostUpdateRequestDto;
import org.blog.model.Post;
import org.blog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public PostListDto getList(String search, int pageNumber, int pageSize) {

        // Парсинг строки поиска
        String titleSubstring = null;
        List<String> requiredTags = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            String[] words = search.trim().split("\\s+");

            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                }

                if (word.startsWith("#")) {
                    // Убираем символ # и добавляем в список тегов
                    String tag = word.substring(1);
                    if (!tag.isEmpty()) {
                        requiredTags.add(tag.toLowerCase());
                    }
                } else {
                    // Склеиваем слова без # через пробел
                    if (titleSubstring == null) {
                        titleSubstring = word;
                    } else {
                        titleSubstring = titleSubstring + " " + word;
                    }
                }
            }
        }

        if (pageNumber < 1)
            pageNumber = 1;

        List<Post> allPosts = postRepository.getList(titleSubstring, requiredTags, (pageNumber - 1) * pageSize, pageSize);
        int totalCount = postRepository.getCount(titleSubstring, requiredTags);
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        return new PostListDto(
                allPosts.stream().map(x -> convertToDto(x, true)).toList(),
                pageNumber > 1,
                pageNumber < totalPages,
                totalPages);
    }

    public Optional<PostDto> getById(long id) {
        Optional<Post> post = postRepository.getById(id);
        return post.map(value -> convertToDto(value, false));
    }

    public PostDto create(PostCreateRequestDto request) {
        Post post = new Post(0, request.getTitle(), request.getText(), request.getTags(), 0, 0);
        long id = postRepository.create(post);

        return getById(id).get();
    }

    public PostDto update(long id, PostUpdateRequestDto request) throws IllegalArgumentException {
        Optional<Post> oppost = postRepository.getById(id);

        if (oppost.isPresent()) {
            Post post = oppost.get();
            post.setTitle(request.getTitle());
            post.setText(request.getText());
            post.setTags(request.getTags());

            postRepository.update(id, post);

            return getById(id).get();
        } else
            throw new IllegalArgumentException();
    }

    public void delete(long id) {
        postRepository.delete(id);
    }

    public int incLikes(long id) throws IllegalArgumentException {
        Optional<Post> oppost = postRepository.getById(id);

        if (oppost.isPresent()) {
            Post post = oppost.get();
            post.setLikesCount(post.getLikesCount() + 1);

            postRepository.update(id, post);

            return getById(id).get().getLikesCount();
        } else
            throw new IllegalArgumentException();
    }

    private PostDto convertToDto(Post post, boolean ellipsis) {
        String truncatedText = post.getText();
        if (ellipsis) {
            // Обрезать text до 128 символов и добавлять '...'
            if (truncatedText != null && truncatedText.length() > 128) {
                truncatedText = truncatedText.substring(0, 128) + "...";
            }
        }

        return new PostDto(
                post.getId(),
                post.getTitle(),
                truncatedText,
                post.getTags(),
                post.getLikesCount(),
                post.getCommentsCount()
        );
    }
}
