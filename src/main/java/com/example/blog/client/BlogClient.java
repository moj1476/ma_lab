package com.example.blog.client;

import com.example.blog.dto.ArticleDTO;
import com.example.blog.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "blogClient", url = "${clients.blog.url:http://localhost:8081}",
        configuration = BlogClientConfig.class)
public interface BlogClient {

    @GetMapping("/api/users/{userId}")
    UserDTO getUser(@PathVariable("userId") Long userId);

    @GetMapping("/api/articles/by-author/{userId}")
    List<ArticleDTO> getArticlesByAuthor(@PathVariable("userId") Long userId);
}
