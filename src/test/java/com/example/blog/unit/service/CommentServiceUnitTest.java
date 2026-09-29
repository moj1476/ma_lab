package com.example.blog.unit.service;

import com.example.blog.dto.CommentCreateDTO;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.CommentUpdateDTO;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.model.Comment;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.CommentService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService tests")
class CommentServiceUnitTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentDTO commentDTO;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(20L);
        comment.setContent("Спасибо за статью");

        commentDTO = new CommentDTO();
        commentDTO.setId(20L);
        commentDTO.setArticleId(10L);
        commentDTO.setAuthorId(2L);
        commentDTO.setContent("Спасибо за статью");
    }

    @Test
    @DisplayName("Создает комментарий при наличии статьи и автора")
    void createsComment() {
        CommentCreateDTO createDTO = createDTO();
        given(articleRepository.existsById(10L)).willReturn(true);
        given(userRepository.existsById(2L)).willReturn(true);
        given(commentMapper.map(createDTO)).willReturn(comment);
        given(commentRepository.save(comment)).willReturn(comment);
        given(commentMapper.map(comment)).willReturn(commentDTO);

        assertThat(commentService.create(createDTO)).isSameAs(commentDTO);
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("Не создает комментарий для отсутствующей статьи")
    void rejectsMissingArticle() {
        CommentCreateDTO createDTO = createDTO();
        given(articleRepository.existsById(10L)).willReturn(false);

        assertThatThrownBy(() -> commentService.create(createDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Article with id 10 not found");
        verifyNoInteractions(userRepository, commentMapper, commentRepository);
    }

    @Test
    @DisplayName("Не создает комментарий для отсутствующего автора")
    void rejectsMissingAuthor() {
        CommentCreateDTO createDTO = createDTO();
        given(articleRepository.existsById(10L)).willReturn(true);
        given(userRepository.existsById(2L)).willReturn(false);

        assertThatThrownBy(() -> commentService.create(createDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id 2 not found");
        verifyNoInteractions(commentMapper, commentRepository);
    }

    @Test
    @DisplayName("Возвращает комментарии статьи")
    void returnsCommentsByArticle() {
        given(articleRepository.existsById(10L)).willReturn(true);
        given(commentRepository.findAllByArticleIdOrderByCreatedAtAsc(10L)).willReturn(List.of(comment));
        given(commentMapper.map(comment)).willReturn(commentDTO);

        assertThat(commentService.findByArticleId(10L)).containsExactly(commentDTO);
    }

    @Test
    @DisplayName("Обновляет комментарий")
    void updatesComment() {
        CommentUpdateDTO updateDTO = new CommentUpdateDTO();
        updateDTO.setContent("Исправленный комментарий");
        given(commentRepository.findById(20L)).willReturn(Optional.of(comment));
        given(commentRepository.save(comment)).willReturn(comment);
        given(commentMapper.map(comment)).willReturn(commentDTO);

        assertThat(commentService.update(20L, updateDTO)).isSameAs(commentDTO);
        verify(commentMapper).update(updateDTO, comment);
    }

    @Test
    @DisplayName("Удаляет найденный комментарий")
    void deletesComment() {
        given(commentRepository.findById(20L)).willReturn(Optional.of(comment));

        commentService.delete(20L);

        verify(commentRepository).delete(comment);
    }

    private CommentCreateDTO createDTO() {
        CommentCreateDTO dto = new CommentCreateDTO();
        dto.setArticleId(10L);
        dto.setAuthorId(2L);
        dto.setContent("Спасибо за статью");
        return dto;
    }
}
