package com.example.blog.unit.controller;

import com.example.blog.controller.UserController;
import com.example.blog.dto.UserCreateDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.dto.UserUpdateDTO;
import com.example.blog.exception.ApiExceptionHandler;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ApiExceptionHandler.class)
@DisplayName("UserController tests")
class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Анна");
        userDTO.setEmail("anna@example.com");
    }

    @Test
    @DisplayName("GET /api/users возвращает список")
    void returnsUsers() throws Exception {
        given(userService.findAll()).willReturn(List.of(userDTO));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("anna@example.com"));
    }

    @Test
    @DisplayName("POST /api/users создает пользователя")
    void createsUser() throws Exception {
        UserCreateDTO request = new UserCreateDTO();
        request.setName("Анна");
        request.setEmail("anna@example.com");
        given(userService.create(any(UserCreateDTO.class))).willReturn(userDTO);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Анна"));
    }

    @Test
    @DisplayName("POST /api/users отклоняет некорректный email")
    void rejectsInvalidUser() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Анна\",\"email\":\"не-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields.email").exists());
    }

    @Test
    @DisplayName("GET /api/users/{id} возвращает пользователя")
    void returnsUserById() throws Exception {
        given(userService.findById(1L)).willReturn(userDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PUT /api/users/{id} обновляет пользователя")
    void updatesUser() throws Exception {
        UserUpdateDTO request = new UserUpdateDTO();
        request.setName("Анна Петрова");
        userDTO.setName("Анна Петрова");
        given(userService.update(any(Long.class), any(UserUpdateDTO.class))).willReturn(userDTO);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Анна Петрова"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} возвращает 204")
    void deletesUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).delete(1L);
    }

    @Test
    @DisplayName("Возвращает 404 для отсутствующего пользователя")
    void returnsNotFound() throws Exception {
        given(userService.findById(99L))
                .willThrow(new ResourceNotFoundException("User with id 99 not found"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User with id 99 not found"));
    }
}

