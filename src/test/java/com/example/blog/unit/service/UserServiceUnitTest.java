package com.example.blog.unit.service;

import com.example.blog.dto.UserCreateDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.dto.UserUpdateDTO;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.UserMapper;
import com.example.blog.model.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService tests")
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Анна");
        user.setEmail("anna@example.com");

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Анна");
        userDTO.setEmail("anna@example.com");
    }

    @Test
    @DisplayName("Создает пользователя")
    void createsUser() {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setName("Анна");
        createDTO.setEmail("anna@example.com");
        given(userMapper.map(createDTO)).willReturn(user);
        given(userRepository.save(user)).willReturn(user);
        given(userMapper.map(user)).willReturn(userDTO);

        UserDTO result = userService.create(createDTO);

        assertThat(result).isSameAs(userDTO);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Возвращает всех пользователей")
    void returnsAllUsers() {
        given(userRepository.findAll()).willReturn(List.of(user));
        given(userMapper.map(user)).willReturn(userDTO);

        assertThat(userService.findAll()).containsExactly(userDTO);
    }

    @Test
    @DisplayName("Обновляет найденного пользователя")
    void updatesUser() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setName("Анна Петрова");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);
        given(userMapper.map(user)).willReturn(userDTO);

        UserDTO result = userService.update(1L, updateDTO);

        assertThat(result).isSameAs(userDTO);
        verify(userMapper).update(updateDTO, user);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Удаляет связанные данные до удаления пользователя")
    void deletesUserWithDependencies() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        userService.delete(1L);

        var ordered = inOrder(commentRepository, articleRepository, userRepository);
        ordered.verify(commentRepository).deleteAllByAuthorId(1L);
        ordered.verify(commentRepository).deleteAllByArticleAuthorId(1L);
        ordered.verify(articleRepository).deleteAllByAuthorId(1L);
        ordered.verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Сообщает об отсутствующем пользователе")
    void rejectsMissingUser() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with id 99 not found");
    }

    @Test
    @DisplayName("Передает mapper тот же объект обновления")
    void passesUpdateDtoToMapper() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setEmail("new@example.com");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(user);
        given(userMapper.map(user)).willReturn(userDTO);

        userService.update(1L, updateDTO);

        ArgumentCaptor<UserUpdateDTO> captor = ArgumentCaptor.forClass(UserUpdateDTO.class);
        verify(userMapper).update(captor.capture(), org.mockito.ArgumentMatchers.eq(user));
        assertThat(captor.getValue().getEmail()).isEqualTo("new@example.com");
    }
}

