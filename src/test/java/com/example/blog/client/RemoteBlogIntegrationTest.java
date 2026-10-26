package com.example.blog.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RemoteBlogIntegrationTest {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final HttpServer SERVER = startServer();

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("clients.blog.url", () -> "http://127.0.0.1:" + SERVER.getAddress().getPort());
        registry.add("spring.cloud.openfeign.client.config.blogClient.connectTimeout", () -> 100);
        registry.add("spring.cloud.openfeign.client.config.blogClient.readTimeout", () -> 100);
    }

    private static HttpServer startServer() {
        try {
            var server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.setExecutor(EXECUTOR);
            server.createContext("/", RemoteBlogIntegrationTest::respond);
            server.start();
            return server;
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void respond(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int status = 200;
        String body;
        if (path.equals("/api/users/503")) {
            exchange.close();
            return;
        }
        if (path.equals("/api/users/504")) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
        if (path.equals("/api/users/500")) {
            status = 500;
            body = "{\"error\":\"Internal error\"}";
        } else if (path.equals("/api/users/1") || path.equals("/api/users/2")) {
            String id = path.substring(path.lastIndexOf('/') + 1);
            body = "{\"id\":" + id + ",\"name\":\"Author\",\"email\":\"author@example.com\"}";
        } else if (path.equals("/api/articles/by-author/1")) {
            body = "[{\"id\":10,\"authorId\":1,\"title\":\"Feign article\",\"slug\":\"feign\",\"content\":\"Text\"}]";
        } else if (path.equals("/api/articles/by-author/2")) {
            body = "[]";
        } else {
            status = 404;
            body = "{\"error\":\"Not found\"}";
        }
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        try (exchange) {
            exchange.sendResponseHeaders(status, bytes.length);
            exchange.getResponseBody().write(bytes);
        }
    }

    @AfterAll
    static void stopServer() {
        SERVER.stop(0);
        EXECUTOR.shutdownNow();
    }

    @Test
    void retrievesUserAndArticlesOverHttp() throws Exception {
        mockMvc.perform(get("/api/remote/users/1/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.articles[0].id").value(10))
                .andExpect(jsonPath("$.articles[0].authorId").value(1));
    }

    @Test
    void returnsEmptyArticlesForAnExistingUser() throws Exception {
        mockMvc.perform(get("/api/remote/users/2/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(2))
                .andExpect(jsonPath("$.articles").isEmpty());
    }

    @Test
    void mapsRemoteNotFoundTo404() throws Exception {
        mockMvc.perform(get("/api/remote/users/99/articles"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void mapsRemoteFailureTo502() throws Exception {
        mockMvc.perform(get("/api/remote/users/500/articles"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502));
    }

    @Test
    void mapsBrokenConnectionTo503() throws Exception {
        mockMvc.perform(get("/api/remote/users/503/articles"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void boundsRemoteReadTimeWithTimeout() throws Exception {
        mockMvc.perform(get("/api/remote/users/504/articles"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }
}
