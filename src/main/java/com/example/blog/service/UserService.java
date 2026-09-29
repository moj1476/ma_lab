package com.example.blog.service;

import com.example.blog.dto.UserCreateDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.dto.UserUpdateDTO;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.UserMapper;
import com.example.blog.model.User;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(userMapper::map).toList();
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        return userMapper.map(getEntity(id));
    }

    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        return userMapper.map(userRepository.save(userMapper.map(dto)));
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        User user = getEntity(id);
        userMapper.update(dto, user);
        return userMapper.map(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        User user = getEntity(id);
        commentRepository.deleteAllByAuthorId(id);
        commentRepository.deleteAllByArticleAuthorId(id);
        articleRepository.deleteAllByAuthorId(id);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public User getEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
    }
}
