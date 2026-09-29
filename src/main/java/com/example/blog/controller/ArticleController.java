package com.example.blog.controller;

import com.example.blog.dto.ArticleCreateDTO;
import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.ArticleUpdateDTO;
import com.example.blog.service.ArticleService;
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
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public List<ArticleDTO> index() {
        return articleService.findAll();
    }

    @GetMapping("/by-author/{authorId}")
    public List<ArticleDTO> byAuthor(@PathVariable Long authorId) {
        return articleService.findByAuthorId(authorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleDTO create(@Valid @RequestBody ArticleCreateDTO dto) {
        return articleService.create(dto);
    }

    @GetMapping("/{id}")
    public ArticleDTO show(@PathVariable Long id) {
        return articleService.findById(id);
    }

    @PutMapping("/{id}")
    public ArticleDTO update(@PathVariable Long id, @Valid @RequestBody ArticleUpdateDTO dto) {
        return articleService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        articleService.delete(id);
    }
}
