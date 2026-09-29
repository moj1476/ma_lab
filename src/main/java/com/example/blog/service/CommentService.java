package com.example.blog.service;

import com.example.blog.dto.CommentCreateDTO;
import com.example.blog.dto.CommentDTO;
import com.example.blog.dto.CommentUpdateDTO;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.CommentMapper;
import com.example.blog.model.Comment;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    public List<CommentDTO> findAll() {
        return commentRepository.findAll().stream().map(commentMapper::map).toList();
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> findByArticleId(Long articleId) {
        ensureArticleExists(articleId);
        return commentRepository.findAllByArticleIdOrderByCreatedAtAsc(articleId)
                .stream().map(commentMapper::map).toList();
    }

    @Transactional(readOnly = true)
    public CommentDTO findById(Long id) {
        return commentMapper.map(getEntity(id));
    }

    @Transactional
    public CommentDTO create(CommentCreateDTO dto) {
        ensureArticleExists(dto.getArticleId());
        ensureUserExists(dto.getAuthorId());
        return commentMapper.map(commentRepository.save(commentMapper.map(dto)));
    }

    @Transactional
    public CommentDTO update(Long id, CommentUpdateDTO dto) {
        Comment comment = getEntity(id);
        if (dto.getArticleId() != null) {
            ensureArticleExists(dto.getArticleId());
        }
        if (dto.getAuthorId() != null) {
            ensureUserExists(dto.getAuthorId());
        }
        commentMapper.update(dto, comment);
        return commentMapper.map(commentRepository.save(comment));
    }

    @Transactional
    public void delete(Long id) {
        commentRepository.delete(getEntity(id));
    }

    private Comment getEntity(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id " + id + " not found"));
    }

    private void ensureArticleExists(Long articleId) {
        if (!articleRepository.existsById(articleId)) {
            throw new ResourceNotFoundException("Article with id " + articleId + " not found");
        }
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }
    }
}
