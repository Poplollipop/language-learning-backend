package com.languagelearning.controller;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RoleAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    private String register(String role) throws Exception {
        String email = "role-" + UUID.randomUUID() + "@example.com";
        String roleField = role == null ? "" : ",\"role\":\"" + role + "\"";
        String body = mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content("{\"email\":\"" + email + "\",\"password\":\"password123\"" + roleField + "}"))
            .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.<String>read(body, "$.token");
    }

    @Test
    void studentCannotCreateCourse() throws Exception {
        String studentToken = register("STUDENT");

        mockMvc.perform(post("/api/courses")
                .header("Authorization", studentToken)
                .contentType("application/json")
                .content("""
                    {"title":"Beginner French","description":"Basics"}"""))
            .andExpect(status().isForbidden());
    }

    @Test
    void teacherCanManageCourseButStudentCanStillReadAndQuiz() throws Exception {
        String teacherToken = register("TEACHER");

        String courseBody = mockMvc.perform(post("/api/courses")
                .header("Authorization", teacherToken)
                .contentType("application/json")
                .content("""
                    {"title":"Beginner French","description":"Basics"}"""))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        Long courseId = ((Number) JsonPath.read(courseBody, "$.id")).longValue();

        mockMvc.perform(post("/api/courses/" + courseId + "/words")
                .header("Authorization", teacherToken)
                .contentType("application/json")
                .content("""
                    {"term":"bonjour","meaning":"hello"}"""))
            .andExpect(status().isCreated());
        mockMvc.perform(post("/api/courses/" + courseId + "/words")
                .header("Authorization", teacherToken)
                .contentType("application/json")
                .content("""
                    {"term":"chat","meaning":"cat"}"""))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/courses/" + courseId + "/words")
                .header("Authorization", teacherToken)
                .contentType("application/json")
                .content("""
                    {"term":"merci","meaning":"thanks"}"""))
            .andExpect(status().isCreated());

        String studentToken = register("STUDENT");

        // students can read courses/words and take quizzes, but cannot mutate them
        mockMvc.perform(get("/api/courses/" + courseId).header("Authorization", studentToken))
            .andExpect(status().isOk());
        mockMvc.perform(get("/api/courses/" + courseId + "/quiz").header("Authorization", studentToken))
            .andExpect(status().isOk());
        mockMvc.perform(post("/api/courses/" + courseId + "/words")
                .header("Authorization", studentToken)
                .contentType("application/json")
                .content("""
                    {"term":"au revoir","meaning":"goodbye"}"""))
            .andExpect(status().isForbidden());

        // teacher can update and delete
        mockMvc.perform(put("/api/courses/" + courseId)
                .header("Authorization", teacherToken)
                .contentType("application/json")
                .content("""
                    {"title":"Beginner French","description":"Updated"}"""))
            .andExpect(status().isOk());
    }

    @Test
    void defaultRoleIsStudentWhenOmitted() throws Exception {
        String token = register(null);

        mockMvc.perform(get("/api/auth/me").header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("STUDENT"));
    }
}
