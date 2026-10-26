package com.example.blog.service;

import com.example.blog.client.BlogClient;
import com.example.blog.dto.UserArticlesDTO;
import com.example.blog.exception.RemoteServiceException;
import com.example.blog.exception.ResourceNotFoundException;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemoteBlogService {

    private final BlogClient blogClient;

    public UserArticlesDTO findUserArticles(Long userId) {
        try {
            var user = blogClient.getUser(userId);
            var articles = blogClient.getArticlesByAuthor(userId);
            return new UserArticlesDTO(user, articles);
        } catch (RetryableException exception) {
            throw new RemoteServiceException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Blog service is unavailable", exception);
        } catch (FeignException.NotFound exception) {
            throw new ResourceNotFoundException("User with id " + userId + " not found in blog service");
        } catch (FeignException exception) {
            throw new RemoteServiceException(HttpStatus.BAD_GATEWAY,
                    "Blog service returned an error", exception);
        }
    }
}
