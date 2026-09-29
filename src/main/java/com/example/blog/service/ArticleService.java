package com.example.blog.service;

import com.example.blog.dto.ArticleCreateDTO;
import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.ArticleUpdateDTO;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.mapper.ArticleMapper;
import com.example.blog.model.Article;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;

    @Transactional(readOnly = true)
    public List<ArticleDTO> findAll() {
        return articleRepository.findAll().stream().map(articleMapper::map).toList();
    }

    @Transactional(readOnly = true)
    public List<ArticleDTO> findByAuthorId(Long authorId) {
        ensureUserExists(authorId);
        return articleRepository.findAllByAuthorIdOrderByCreatedAtDesc(authorId)
                .stream().map(articleMapper::map).toList();
    }

    @Transactional(readOnly = true)
    public ArticleDTO findById(Long id) {
        return articleMapper.map(getEntity(id));
    }

    @Transactional
    public ArticleDTO create(ArticleCreateDTO dto) {
        ensureUserExists(dto.getAuthorId());
        return articleMapper.map(articleRepository.save(articleMapper.map(dto)));
    }

    @Transactional
    public ArticleDTO update(Long id, ArticleUpdateDTO dto) {
        Article article = getEntity(id);
        if (dto.getAuthorId() != null) {
            ensureUserExists(dto.getAuthorId());
        }
        articleMapper.update(dto, article);
        return articleMapper.map(articleRepository.save(article));
    }

    @Transactional
    public void delete(Long id) {
        Article article = getEntity(id);
        commentRepository.deleteAllByArticleId(id);
        articleRepository.delete(article);
    }

    @Transactional(readOnly = true)
    public Article getEntity(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article with id " + id + " not found"));
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User with id " + userId + " not found");
        }
    }
}
