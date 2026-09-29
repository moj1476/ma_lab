package com.example.blog.unit.repository;

import com.example.blog.model.Article;
import com.example.blog.model.Comment;
import com.example.blog.model.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@DisplayName("CommentRepository tests")
class CommentRepositoryUnitTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    private User articleAuthor;
    private User commentAuthor;
    private Article article;

    @BeforeEach
    void setUp() {
        articleAuthor = userRepository.saveAndFlush(user("Автор статьи", "article.author@example.com"));
        commentAuthor = userRepository.saveAndFlush(user("Комментатор", "comment.author@example.com"));
        article = new Article();
        article.setAuthor(articleAuthor);
        article.setTitle("Тестовая статья");
        article.setSlug("test-article");
        article.setContent("Содержимое");
        article = articleRepository.saveAndFlush(article);
    }

    @Test
    @DisplayName("Сохраняет комментарий")
    void savesComment() {
        Comment saved = commentRepository.saveAndFlush(comment("Полезная статья"));

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getArticle().getId()).isEqualTo(article.getId());
        assertThat(saved.getAuthor().getId()).isEqualTo(commentAuthor.getId());
    }

    @Test
    @DisplayName("Возвращает комментарии статьи от старых к новым")
    void findsCommentsByArticle() {
        Comment first = commentRepository.saveAndFlush(comment("Первый"));
        Comment second = commentRepository.saveAndFlush(comment("Второй"));

        List<Comment> result = commentRepository.findAllByArticleIdOrderByCreatedAtAsc(article.getId());

        assertThat(result).extracting(Comment::getId)
                .containsExactly(first.getId(), second.getId());
    }

    @Test
    @DisplayName("Обновляет комментарий")
    void updatesComment() {
        Comment saved = commentRepository.saveAndFlush(comment("Исходный текст"));
        saved.setContent("Исправленный текст");

        Comment updated = commentRepository.saveAndFlush(saved);

        assertThat(updated.getContent()).isEqualTo("Исправленный текст");
    }

    @Test
    @DisplayName("Удаляет комментарии статьи")
    void deletesCommentsByArticle() {
        commentRepository.saveAndFlush(comment("Первый"));
        commentRepository.saveAndFlush(comment("Второй"));

        commentRepository.deleteAllByArticleId(article.getId());
        commentRepository.flush();

        assertThat(commentRepository.findAllByArticleIdOrderByCreatedAtAsc(article.getId())).isEmpty();
    }

    @Test
    @DisplayName("Удаляет комментарии пользователя")
    void deletesCommentsByAuthor() {
        commentRepository.saveAndFlush(comment("Комментарий"));

        commentRepository.deleteAllByAuthorId(commentAuthor.getId());
        commentRepository.flush();

        assertThat(commentRepository.findAll()).isEmpty();
    }

    private User user(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private Comment comment(String content) {
        Comment comment = new Comment();
        comment.setArticle(article);
        comment.setAuthor(commentAuthor);
        comment.setContent(content);
        return comment;
    }
}
