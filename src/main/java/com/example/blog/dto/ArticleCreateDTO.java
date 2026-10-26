package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleCreateDTO {

    @NotNull
    @Schema(description = "Идентификатор автора", example = "1")
    private Long authorId;

    @NotBlank
    @Size(max = 200)
    @Schema(description = "Заголовок статьи", example = "Первая статья")
    private String title;

    @NotBlank
    @Size(max = 160)
    @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*",
            message = "сообщение должно содержать разрешенные символы")
    @Schema(description = "Уникальный адрес статьи", example = "first-article")
    private String slug;

    @NotBlank
    @Schema(description = "Содержимое", example = "Текст статьи")
    private String content;
}
