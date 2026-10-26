package com.example.blog.controller;

import com.example.blog.dto.CommentCreateDTO;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.CommentUpdateDTO;
import com.example.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Комментарии", description = "Управление комментариями и выборка по статье")
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(summary = "Получить все комментарии")
    public List<CommentDTO> index() {
        return commentService.findAll();
    }

    @GetMapping("/by-article/{articleId}")
    @Operation(summary = "Получить комментарии статьи")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Ресурс не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public List<CommentDTO> byArticle(@PathVariable Long articleId) {
        return commentService.findByArticleId(articleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать комментарий")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public CommentDTO create(@Valid @RequestBody CommentCreateDTO dto) {
        return commentService.create(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить комментарий по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Ресурс не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public CommentDTO show(@PathVariable Long id) {
        return commentService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить комментарий", description = "Передайте только изменяемые поля. Поля со значением null сохраняются без изменений.")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public CommentDTO update(@PathVariable Long id, @Valid @RequestBody CommentUpdateDTO dto) {
        return commentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить комментарий")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Ресурс не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}
