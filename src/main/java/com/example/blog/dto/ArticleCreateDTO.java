package com.example.blog.dto;

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
    private Long authorId;

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 160)
    @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*",
            message = "сообщение должно содержать разрешенные символы")
    private String slug;

    @NotBlank
    private String content;
}
