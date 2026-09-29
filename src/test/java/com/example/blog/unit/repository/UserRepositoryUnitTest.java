package com.example.blog.unit.repository;

import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@DisplayName("UserRepository tests")
class UserRepositoryUnitTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Сохраняет пользователя и назначает идентификатор")
    void savesUser() {
        User saved = userRepository.saveAndFlush(user("Анна", "anna@example.com"));

        assertThat(saved.getId()).isPositive();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Находит пользователя по электронной почте")
    void findsUserByEmail() {
        userRepository.saveAndFlush(user("Иван", "ivan@example.com"));

        Optional<User> found = userRepository.findByEmail("ivan@example.com");

        assertThat(found).isPresent();
        assertThat(found.orElseThrow().getName()).isEqualTo("Иван");
    }

    @Test
    @DisplayName("Обновляет имя пользователя")
    void updatesUser() {
        User saved = userRepository.saveAndFlush(user("Старое имя", "user@example.com"));
        saved.setName("Новое имя");

        User updated = userRepository.saveAndFlush(saved);

        assertThat(updated.getName()).isEqualTo("Новое имя");
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("Удаляет пользователя")
    void deletesUser() {
        User saved = userRepository.saveAndFlush(user("Петр", "petr@example.com"));

        userRepository.delete(saved);
        userRepository.flush();

        assertThat(userRepository.findById(saved.getId())).isEmpty();
    }

    private User user(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}

