package com.languagelearning.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String REGISTER_BODY = """
        {"email":"student@example.com","password":"password123"}""";

    @Test
    void registerLoginAndAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(REGISTER_BODY))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").isNotEmpty());

        // duplicate email must be rejected
        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(REGISTER_BODY))
            .andExpect(status().isConflict());

        // wrong password must be rejected
        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("""
                    {"email":"student@example.com","password":"wrong-password"}"""))
            .andExpect(status().isUnauthorized());

        // protected endpoint rejects missing token
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isForbidden());

        // correct login returns a token that unlocks the protected endpoint
        String loginBody = mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(REGISTER_BODY))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        String token = JsonPath.read(loginBody, "$.token");

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("student@example.com"));
    }
}
