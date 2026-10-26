package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {

    @NotNull
    @Schema(description = "Идентификатор статьи", example = "1")
    private Long articleId;

    @NotNull
    @Schema(description = "Идентификатор автора", example = "1")
    private Long authorId;

    @NotBlank
    @Size(max = 2000)
    @Schema(description = "Текст комментария", example = "Спасибо за статью")
    private String content;
}
