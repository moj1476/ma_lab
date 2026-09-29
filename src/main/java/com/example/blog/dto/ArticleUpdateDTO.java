package com.example.blog.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArticleUpdateDTO {

    private Long authorId;

    @Size(min = 1, max = 200)
    private String title;

    @Size(min = 1, max = 160)
    @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*",
            message = "сообщение должно содержать разрешенные символы")
    private String slug;

    @Size(min = 1)
    private String content;
}
