package com.example.blog.controller;

import com.example.blog.dto.CommentCreateDTO;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.CommentUpdateDTO;
import com.example.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDTO> index() {
        return commentService.findAll();
    }

    @GetMapping("/by-article/{articleId}")
    public List<CommentDTO> byArticle(@PathVariable Long articleId) {
        return commentService.findByArticleId(articleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO create(@Valid @RequestBody CommentCreateDTO dto) {
        return commentService.create(dto);
    }

    @GetMapping("/{id}")
    public CommentDTO show(@PathVariable Long id) {
        return commentService.findById(id);
    }

    @PutMapping("/{id}")
    public CommentDTO update(@PathVariable Long id, @Valid @RequestBody CommentUpdateDTO dto) {
        return commentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}
