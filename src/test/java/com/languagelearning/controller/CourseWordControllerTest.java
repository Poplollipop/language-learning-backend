package com.languagelearning.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseWordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String authToken() throws Exception {
        String body = mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content("""
                    {"email":"teacher@example.com","password":"password123","role":"TEACHER"}"""))
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    @Test
    void courseAndWordCrudFlow() throws Exception {
        String token = "Bearer " + authToken();

        // anonymous requests are rejected
        mockMvc.perform(get("/api/courses")).andExpect(status().isForbidden());

        // create a course
        String courseBody = mockMvc.perform(post("/api/courses")
                .header("Authorization", token)
                .contentType("application/json")
                .content("""
                    {"title":"Beginner Spanish","description":"Basics"}"""))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        Long courseId = ((Number) JsonPath.read(courseBody, "$.id")).longValue();

        // add a word under the course
        String wordBody = mockMvc.perform(post("/api/courses/" + courseId + "/words")
                .header("Authorization", token)
                .contentType("application/json")
                .content("""
                    {"term":"hola","meaning":"hello","example":"Hola, amigo"}"""))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.courseId").value(courseId))
            .andReturn().getResponse().getContentAsString();
        Long wordId = ((Number) JsonPath.read(wordBody, "$.id")).longValue();

        // word under a non-existent course is rejected
        mockMvc.perform(post("/api/courses/999999/words")
                .header("Authorization", token)
                .contentType("application/json")
                .content("""
                    {"term":"x","meaning":"y"}"""))
            .andExpect(status().isNotFound());

        // list words for the course
        mockMvc.perform(get("/api/courses/" + courseId + "/words").header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].term").value("hola"));

        // update the word
        mockMvc.perform(put("/api/words/" + wordId)
                .header("Authorization", token)
                .contentType("application/json")
                .content("""
                    {"term":"hola","meaning":"hi","example":"Hola!"}"""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.meaning").value("hi"));

        // deleting the course cascades to its words
        mockMvc.perform(delete("/api/courses/" + courseId).header("Authorization", token))
            .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/words/" + wordId).header("Authorization", token))
            .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/courses/" + courseId).header("Authorization", token))
            .andExpect(status().isNotFound());
    }
}
