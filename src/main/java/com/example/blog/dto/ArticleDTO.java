package com.example.blog.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleDTO {
    private Long id;
    private Long authorId;
    private String title;
    private String slug;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
