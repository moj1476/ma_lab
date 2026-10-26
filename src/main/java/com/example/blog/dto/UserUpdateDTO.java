package com.example.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

    @Size(min = 1, max = 120)
    @Schema(description = "Имя пользователя", example = "Анна Петрова")
    private String name;

    @Email
    @Size(max = 180)
    @Schema(description = "Электронная почта", example = "anna@example.com")
    private String email;
}
