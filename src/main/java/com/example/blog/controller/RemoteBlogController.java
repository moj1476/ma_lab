package com.example.blog.controller;

import com.example.blog.dto.UserArticlesDTO;
import com.example.blog.service.RemoteBlogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/remote/users")
@RequiredArgsConstructor
@Tag(name = "Feign", description = "Запросы к отдельному серверу блога")
public class RemoteBlogController {

    private final RemoteBlogService remoteBlogService;

    @GetMapping("/{userId}/articles")
    @Operation(summary = "Получить пользователя и его статьи через Feign")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "502", description = "Ошибка ответа сервера блога",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object"))),
            @ApiResponse(responseCode = "503", description = "Сервер блога недоступен",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "object")))
    })
    public UserArticlesDTO userArticles(@PathVariable Long userId) {
        return remoteBlogService.findUserArticles(userId);
    }
}
