package com.example.blog.controller;

import com.example.blog.dto.ArticleCreateDTO;
import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.ArticleUpdateDTO;
import com.example.blog.service.ArticleService;
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

@Tag(name = "Статьи", description = "Управление статьями и выборка по автору")
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "Получить все статьи")
    public List<ArticleDTO> index() {
        return articleService.findAll();
    }

    @GetMapping("/by-author/{authorId}")
    @Operation(summary = "Получить статьи автора")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Ресурс не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public List<ArticleDTO> byAuthor(@PathVariable Long authorId) {
        return articleService.findByAuthorId(authorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать статью")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "409", description = "Нарушение уникальности данных",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ArticleDTO create(@Valid @RequestBody ArticleCreateDTO dto) {
        return articleService.create(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить статью по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Ресурс не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ArticleDTO show(@PathVariable Long id) {
        return articleService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить статью", description = "Передайте только изменяемые поля. Поля со значением null сохраняются без изменений.")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "404", description = "Ресурс не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "409", description = "Нарушение уникальности данных",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public ArticleDTO update(@PathVariable Long id, @Valid @RequestBody ArticleUpdateDTO dto) {
        return articleService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить статью", description = "Также удаляются комментарии статьи.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Ресурс не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public void delete(@PathVariable Long id) {
        articleService.delete(id);
    }
}
