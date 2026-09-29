package com.example.blog.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateDTO {
    private Long articleId;
    private Long authorId;

    @Size(min = 1, max = 2000)
    private String content;
}
