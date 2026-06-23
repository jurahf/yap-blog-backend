package org.blog.controller;

import org.blog.dtos.*;
import org.blog.service.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final FilesService filesService;
    private final CommentsService commentsService;

    public PostController(PostService postService, FilesService filesService, CommentsService commentsService) {

        this.postService = postService;
        this.filesService = filesService;
        this.commentsService = commentsService;
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

    @GetMapping("/{id}/comments")
    public List<CommentDto> getComments(@PathVariable("id") long postId) {
        return commentsService.getByPostId(postId);
    }

    @GetMapping("/{postId}/comments/{id}")
    public ResponseEntity<CommentDto> getComments(@PathVariable("postId") long postId, @PathVariable("id") long id) {
        var result = commentsService.getById(postId, id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/comments")
    public CommentDto createComments(@PathVariable("id") long postId, @RequestBody CommentCreateRequestDto requestDto) {
        return commentsService.create(postId, requestDto);
    }

    @PutMapping("/{postId}/comments/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable("postId") long postId,
            @PathVariable("id") long id,
            @RequestBody CommentUpdateRequestDto requestDto) {
        try {
            var result = commentsService.update(postId, id, requestDto);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{postId}/comments/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable("postId") long postId,
            @PathVariable("id") long id) {
        commentsService.delete(postId, id);
        return ResponseEntity.ok().build();
    }
}
