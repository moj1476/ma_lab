package com.example.blog.documentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void documentsEveryBlogOperationAndRequestSchema() throws Exception {
        String response = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("API блога"))
                .andReturn().getResponse().getContentAsString();
        JsonNode document = objectMapper.readTree(response);
        JsonNode paths = document.path("paths");

        for (String resource : new String[]{"users", "articles", "comments"}) {
            JsonNode collection = paths.path("/api/" + resource);
            JsonNode item = paths.path("/api/" + resource + "/{id}");
            assertThat(collection.has("get")).isTrue();
            assertThat(collection.path("post").path("responses").has("201")).isTrue();
            assertThat(item.has("get")).isTrue();
            assertThat(item.has("put")).isTrue();
            assertThat(item.path("delete").path("responses").has("204")).isTrue();
            assertThat(item.path("get").path("parameters").get(0).path("required").asBoolean()).isTrue();
        }
        assertThat(paths.path("/api/articles/by-author/{authorId}").has("get")).isTrue();
        assertThat(paths.path("/api/comments/by-article/{articleId}").has("get")).isTrue();
        assertThat(paths.size()).isEqualTo(8);

        JsonNode schemas = document.path("components").path("schemas");
        for (String entity : new String[]{"User", "Article", "Comment"}) {
            assertThat(schemas.has(entity + "DTO")).isTrue();
            assertThat(schemas.has(entity + "CreateDTO")).isTrue();
            assertThat(schemas.has(entity + "UpdateDTO")).isTrue();
        }
        assertThat(schemas.path("UserCreateDTO").path("properties").path("email").path("example").asText())
                .isEqualTo("anna@example.com");
        assertThat(schemas.path("UserCreateDTO").path("required").toString()).contains("name", "email");
        assertThat(schemas.path("ArticleCreateDTO").path("properties").path("slug").has("pattern")).isTrue();
    }

    @Test
    void servesSwaggerUiAndItsConfiguration() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Swagger UI")));
        mockMvc.perform(get("/v3/api-docs/swagger-config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value("/v3/api-docs"));
    }

    @Test
    void exportsYamlSpecification() throws Exception {
        mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/users:")));
    }
}
