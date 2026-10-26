package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleDTO {
    @Schema(description = "Идентификатор", example = "1")
    private Long id;
    @Schema(description = "Идентификатор автора", example = "1")
    private Long authorId;
    @Schema(description = "Заголовок статьи", example = "Первая статья")
    private String title;
    @Schema(description = "Уникальный адрес статьи", example = "first-article")
    private String slug;
    @Schema(description = "Содержимое", example = "Текст статьи")
    private String content;
    @Schema(description = "Дата создания", example = "2026-10-27T12:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "Дата изменения", example = "2026-10-27T12:00:00")
    private LocalDateTime updatedAt;
}
