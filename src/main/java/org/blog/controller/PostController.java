package org.blog.controller;

import org.blog.dtos.PostDto;
import org.blog.dtos.PostListDto;
import org.blog.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {

        this.postService = postService;
    }


    @GetMapping("hello")
    public String test() {
        return "Hello world";
    }

    @GetMapping(value = "")
    public PostListDto getList(
            @RequestParam(value = "search") String search,
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize) {
        return postService.getList(URLDecoder.decode(search, StandardCharsets.UTF_8), pageNumber, pageSize);    // декодируем чтобы обработать символ #
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable(name = "id") long id) {
        var result = postService.getById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


}
