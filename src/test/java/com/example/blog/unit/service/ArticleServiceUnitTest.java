package com.example.blog.unit.service;

import com.example.blog.dto.ArticleCreateDTO;
import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.ArticleUpdateDTO;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.ArticleMapper;
import com.example.blog.model.Article;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.ArticleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleService tests")
class ArticleServiceUnitTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleService articleService;

    private Article article;
    private ArticleDTO articleDTO;

    @BeforeEach
    void setUp() {
        article = new Article();
        article.setId(10L);
        article.setTitle("JUnit 5");
        article.setSlug("junit-5");
        article.setContent("Текст статьи");

        articleDTO = new ArticleDTO();
        articleDTO.setId(10L);
        articleDTO.setAuthorId(1L);
        articleDTO.setTitle("JUnit 5");
        articleDTO.setSlug("junit-5");
        articleDTO.setContent("Текст статьи");
    }

    @Test
    @DisplayName("Создает статью существующего автора")
    void createsArticle() {
        ArticleCreateDTO createDTO = createDTO();
        given(userRepository.existsById(1L)).willReturn(true);
        given(articleMapper.map(createDTO)).willReturn(article);
        given(articleRepository.save(article)).willReturn(article);
        given(articleMapper.map(article)).willReturn(articleDTO);

        ArticleDTO result = articleService.create(createDTO);

        assertThat(result).isSameAs(articleDTO);
        verify(articleRepository).save(article);
    }

    @Test
    @DisplayName("Не создает статью для отсутствующего автора")
    void rejectsArticleForMissingAuthor() {
        ArticleCreateDTO createDTO = createDTO();
        given(userRepository.existsById(1L)).willReturn(false);

        assertThatThrownBy(() -> articleService.create(createDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id 1 not found");
        verifyNoInteractions(articleMapper, articleRepository);
    }

    @Test
    @DisplayName("Возвращает статьи автора")
    void returnsArticlesByAuthor() {
        given(userRepository.existsById(1L)).willReturn(true);
        given(articleRepository.findAllByAuthorIdOrderByCreatedAtDesc(1L)).willReturn(List.of(article));
        given(articleMapper.map(article)).willReturn(articleDTO);

        assertThat(articleService.findByAuthorId(1L)).containsExactly(articleDTO);
    }

    @Test
    @DisplayName("Обновляет статью")
    void updatesArticle() {
        ArticleUpdateDTO updateDTO = new ArticleUpdateDTO();
        updateDTO.setTitle("Mockito");
        given(articleRepository.findById(10L)).willReturn(Optional.of(article));
        given(articleRepository.save(article)).willReturn(article);
        given(articleMapper.map(article)).willReturn(articleDTO);

        assertThat(articleService.update(10L, updateDTO)).isSameAs(articleDTO);
        verify(articleMapper).update(updateDTO, article);
    }

    @Test
    @DisplayName("Удаляет комментарии перед статьей")
    void deletesArticleWithComments() {
        given(articleRepository.findById(10L)).willReturn(Optional.of(article));

        articleService.delete(10L);

        var ordered = inOrder(commentRepository, articleRepository);
        ordered.verify(commentRepository).deleteAllByArticleId(10L);
        ordered.verify(articleRepository).delete(article);
    }

    private ArticleCreateDTO createDTO() {
        ArticleCreateDTO dto = new ArticleCreateDTO();
        dto.setAuthorId(1L);
        dto.setTitle("JUnit 5");
        dto.setSlug("junit-5");
        dto.setContent("Текст статьи");
        return dto;
    }
}

