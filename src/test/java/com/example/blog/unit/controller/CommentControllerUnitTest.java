package com.example.blog.unit.controller;

import com.example.blog.controller.CommentController;
import com.example.blog.dto.CommentCreateDTO;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.CommentUpdateDTO;
import com.example.blog.exception.ApiExceptionHandler;
import com.example.blog.service.CommentService;
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

@WebMvcTest(CommentController.class)
@Import(ApiExceptionHandler.class)
@DisplayName("CommentController tests")
class CommentControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CommentService commentService;

    private CommentDTO commentDTO;

    @BeforeEach
    void setUp() {
        commentDTO = new CommentDTO();
        commentDTO.setId(20L);
        commentDTO.setArticleId(10L);
        commentDTO.setAuthorId(2L);
        commentDTO.setContent("Спасибо за статью");
    }

    @Test
    @DisplayName("GET /api/comments возвращает список")
    void returnsComments() throws Exception {
        given(commentService.findAll()).willReturn(List.of(commentDTO));

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Спасибо за статью"));
    }

    @Test
    @DisplayName("GET /api/comments/by-article/{id} фильтрует по статье")
    void returnsCommentsByArticle() throws Exception {
        given(commentService.findByArticleId(10L)).willReturn(List.of(commentDTO));

        mockMvc.perform(get("/api/comments/by-article/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].articleId").value(10));
    }

    @Test
    @DisplayName("POST /api/comments создает комментарий")
    void createsComment() throws Exception {
        CommentCreateDTO request = new CommentCreateDTO();
        request.setArticleId(10L);
        request.setAuthorId(2L);
        request.setContent("Спасибо за статью");
        given(commentService.create(any(CommentCreateDTO.class))).willReturn(commentDTO);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20));
    }

    @Test
    @DisplayName("POST /api/comments отклоняет пустой текст")
    void rejectsBlankComment() throws Exception {
        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"articleId\":10,\"authorId\":2,\"content\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.content").exists());
    }

    @Test
    @DisplayName("PUT /api/comments/{id} обновляет комментарий")
    void updatesComment() throws Exception {
        CommentUpdateDTO request = new CommentUpdateDTO();
        request.setContent("Исправленный текст");
        commentDTO.setContent("Исправленный текст");
        given(commentService.update(any(Long.class), any(CommentUpdateDTO.class))).willReturn(commentDTO);

        mockMvc.perform(put("/api/comments/20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Исправленный текст"));
    }

    @Test
    @DisplayName("DELETE /api/comments/{id} возвращает 204")
    void deletesComment() throws Exception {
        mockMvc.perform(delete("/api/comments/20"))
                .andExpect(status().isNoContent());

        verify(commentService).delete(20L);
    }
}
