package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserDTO {
    @Schema(description = "Идентификатор", example = "1")
    private Long id;
    @Schema(description = "Имя пользователя", example = "Анна Петрова")
    private String name;
    @Schema(description = "Электронная почта", example = "anna@example.com")
    private String email;
    @Schema(description = "Дата создания", example = "2026-10-27T12:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "Дата изменения", example = "2026-10-27T12:00:00")
    private LocalDateTime updatedAt;
}
