package org.blog.controller;

import org.blog.dtos.*;
import org.blog.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "<h1>Hello, world!</h1>";
    }

    @GetMapping(value = "")
    public PostListDto getList(
            @RequestParam(value = "search") String search,
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize) {
        return service.getList(search, pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable(name = "id") long id) {
        var result = service.getById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public PostDto create(@RequestBody PostRequestDto requestDto) {
        return service.create(requestDto);
    }

}
