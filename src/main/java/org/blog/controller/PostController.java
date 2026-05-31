package org.blog.controller;

import org.blog.dtos.*;
import org.blog.service.FilesService;
import org.blog.service.PostService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final FilesService filesService;

    public PostController(PostService postService, FilesService filesService) {

        this.postService = postService;
        this.filesService = filesService;
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
        return postService.getList(search, pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getById(@PathVariable(name = "id") long id) {
        var result = postService.getById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public PostDto create(@RequestBody PostCreateRequestDto requestDto) {
        return postService.create(requestDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> update (@PathVariable(name = "id") long id, @RequestBody PostUpdateRequestDto requestDto) {
        try {
            var result = postService.update(id, requestDto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PostDto> delete(@PathVariable(name = "id") long id) {
        postService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Integer> incLikes(@PathVariable(name = "id") long id) {
        try {
            int result = postService.incLikes(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/image")
    public ResponseEntity<?> uploadFile(
            @PathVariable("id") long postId,
            @RequestParam("image") MultipartFile imageFile) {
        filesService.upload(postId, imageFile);

        return ResponseEntity.ok().build(); // 200 OK
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getImage(@PathVariable("id") long postId) {
        try {
            Resource file = filesService.download(postId);

            return ResponseEntity.ok()
                    //.contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(file);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
