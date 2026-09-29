package com.example.blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

    @Size(min = 1, max = 120)
    private String name;

    @Email
    @Size(max = 180)
    private String email;
}
