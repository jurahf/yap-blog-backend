package org.blog.controller;

import org.blog.model.Post;
import org.blog.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public List<Post> getList(
            @RequestParam(value = "search") String search,
            @RequestParam(value = "pageNumber") int pageNumber,
            @RequestParam(value = "pageSize") int pageSize) {
        // TODO:
        return service.getList();
    }

}
