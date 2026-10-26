package com.example.blog.unit.repository;

import com.example.blog.model.Article;
import com.example.blog.model.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@DisplayName("ArticleRepository tests")
class ArticleRepositoryUnitTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    private User author;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setName("Автор");
        author.setEmail("author@example.com");
        author = userRepository.saveAndFlush(author);
    }

    @Test
    @DisplayName("Сохраняет статью")
    void savesArticle() {
        Article saved = articleRepository.saveAndFlush(article("Первая статья", "first-article"));

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getAuthor().getId()).isEqualTo(author.getId());
    }

    @Test
    @DisplayName("Находит статью по slug")
    void findsArticleBySlug() {
        articleRepository.saveAndFlush(article("Spring Boot", "spring-boot"));

        assertThat(articleRepository.findBySlug("spring-boot"))
                .isPresent()
                .get()
                .extracting(Article::getTitle)
                .isEqualTo("Spring Boot");
    }

    @Test
    @DisplayName("Возвращает статьи автора от новых к старым")
    void findsArticlesByAuthor() {
        Article first = articleRepository.saveAndFlush(article("Первая", "first"));
        Article second = articleRepository.saveAndFlush(article("Вторая", "second"));

        List<Article> result = articleRepository.findAllByAuthorIdOrderByCreatedAtDescIdDesc(author.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Article::getId)
                .containsExactly(second.getId(), first.getId());
    }

    @Test
    @DisplayName("Обновляет содержимое статьи")
    void updatesArticle() {
        Article saved = articleRepository.saveAndFlush(article("Черновик", "draft"));
        saved.setContent("Обновленный текст статьи");

        Article updated = articleRepository.saveAndFlush(saved);

        assertThat(updated.getContent()).isEqualTo("Обновленный текст статьи");
    }

    @Test
    @DisplayName("Удаляет все статьи автора")
    void deletesArticlesByAuthor() {
        articleRepository.saveAndFlush(article("Первая", "delete-first"));
        articleRepository.saveAndFlush(article("Вторая", "delete-second"));

        articleRepository.deleteAllByAuthorId(author.getId());
        articleRepository.flush();

        assertThat(articleRepository.findAllByAuthorIdOrderByCreatedAtDescIdDesc(author.getId())).isEmpty();
    }

    private Article article(String title, String slug) {
        Article article = new Article();
        article.setAuthor(author);
        article.setTitle(title);
        article.setSlug(slug);
        article.setContent("Тестовое содержимое статьи");
        return article;
    }
}

