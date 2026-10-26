package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Пользователь и его статьи, полученные через Feign")
public record UserArticlesDTO(UserDTO user, List<ArticleDTO> articles) {
}
