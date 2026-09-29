package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDTO {

    @NotNull
    private Long articleId;

    @NotNull
    private Long authorId;

    @NotBlank
    @Size(max = 2000)
    private String content;
}
