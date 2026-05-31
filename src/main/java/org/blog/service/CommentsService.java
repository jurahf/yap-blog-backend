package org.blog.service;

import org.blog.dtos.CommentCreateRequestDto;
import org.blog.dtos.CommentDto;
import org.blog.dtos.CommentUpdateRequestDto;
import org.blog.model.Comment;
import org.blog.model.Post;
import org.blog.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentsService {
    private final CommentRepository commentRepository;

    public CommentsService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentDto> getByPostId(long postId) {
        List<Comment> comments = commentRepository.getList(postId);

        return comments.stream()
                .map(x -> new CommentDto(x.getId(), x.getText(), x.getPostId()))
                .toList();
    }

    public Optional<CommentDto> getById(long postId, long id) {
        Optional<Comment> comment = commentRepository.getById(postId, id);
        return comment.map(x -> new CommentDto(x.getId(), x.getText(), x.getPostId()));
    }

    public CommentDto create(long postId, CommentCreateRequestDto request) {
        Comment comment = new Comment(0, request.getText(), request.getPostId());
        long id = commentRepository.create(comment);

        return getById(postId, id).get();
    }

    public CommentDto update(long postId, long id, CommentUpdateRequestDto request) {
        Optional<Comment> opt = commentRepository.getById(postId, id);

        if (opt.isPresent()) {
            Comment comment = opt.get();
            comment.setText(request.getText());

            commentRepository.update(postId, id, comment);

            return getById(postId, id).get();
        } else
            throw new IllegalArgumentException();
    }

    public void delete(long postId, long id) {
        commentRepository.delete(postId, id);
    }
}
