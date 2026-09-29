package com.example.blog.unit.controller;

import com.example.blog.controller.ArticleController;
import com.example.blog.dto.ArticleCreateDTO;
import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.ArticleUpdateDTO;
import com.example.blog.exception.ApiExceptionHandler;
import com.example.blog.service.ArticleService;
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

@WebMvcTest(ArticleController.class)
@Import(ApiExceptionHandler.class)
@DisplayName("ArticleController tests")
class ArticleControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ArticleService articleService;

    private ArticleDTO articleDTO;

    @BeforeEach
    void setUp() {
        articleDTO = new ArticleDTO();
        articleDTO.setId(10L);
        articleDTO.setAuthorId(1L);
        articleDTO.setTitle("JUnit 5");
        articleDTO.setSlug("junit-5");
        articleDTO.setContent("Текст статьи");
    }

    @Test
    @DisplayName("GET /api/articles возвращает список")
    void returnsArticles() throws Exception {
        given(articleService.findAll()).willReturn(List.of(articleDTO));

        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("junit-5"));
    }

    @Test
    @DisplayName("GET /api/articles/by-author/{id} фильтрует по автору")
    void returnsArticlesByAuthor() throws Exception {
        given(articleService.findByAuthorId(1L)).willReturn(List.of(articleDTO));

        mockMvc.perform(get("/api/articles/by-author/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorId").value(1));
    }

    @Test
    @DisplayName("POST /api/articles создает статью")
    void createsArticle() throws Exception {
        ArticleCreateDTO request = new ArticleCreateDTO();
        request.setAuthorId(1L);
        request.setTitle("JUnit 5");
        request.setSlug("junit-5");
        request.setContent("Текст статьи");
        given(articleService.create(any(ArticleCreateDTO.class))).willReturn(articleDTO);

        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @DisplayName("POST /api/articles отклоняет неправильный slug")
    void rejectsInvalidSlug() throws Exception {
        mockMvc.perform(post("/api/articles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"authorId\":1,\"title\":\"Статья\",\"slug\":\"Wrong Slug\",\"content\":\"Текст\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.slug").exists());
    }

    @Test
    @DisplayName("PUT /api/articles/{id} обновляет статью")
    void updatesArticle() throws Exception {
        ArticleUpdateDTO request = new ArticleUpdateDTO();
        request.setTitle("Mockito");
        articleDTO.setTitle("Mockito");
        given(articleService.update(any(Long.class), any(ArticleUpdateDTO.class))).willReturn(articleDTO);

        mockMvc.perform(put("/api/articles/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Mockito"));
    }

    @Test
    @DisplayName("DELETE /api/articles/{id} возвращает 204")
    void deletesArticle() throws Exception {
        mockMvc.perform(delete("/api/articles/10"))
                .andExpect(status().isNoContent());

        verify(articleService).delete(10L);
    }
}

