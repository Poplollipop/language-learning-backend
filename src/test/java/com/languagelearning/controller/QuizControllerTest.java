package com.languagelearning.controller;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String authToken() throws Exception {
        String email = "quiz-" + UUID.randomUUID() + "@example.com";
        String body = mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content("{\"email\":\"" + email + "\",\"password\":\"password123\",\"role\":\"TEACHER\"}"))
            .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.<String>read(body, "$.token");
    }

    private Long createCourse(String token) throws Exception {
        String body = mockMvc.perform(post("/api/courses")
                .header("Authorization", token)
                .contentType("application/json")
                .content("""
                    {"title":"Beginner Spanish","description":"Basics"}"""))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private Long addWord(String token, Long courseId, String term, String meaning) throws Exception {
        String body = mockMvc.perform(post("/api/courses/" + courseId + "/words")
                .header("Authorization", token)
                .contentType("application/json")
                .content("{\"term\":\"" + term + "\",\"meaning\":\"" + meaning + "\"}"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    @Test
    void quizNotAvailableWhenCourseHasFewerThanTwoWords() throws Exception {
        String token = authToken();
        Long courseId = createCourse(token);
        addWord(token, courseId, "hola", "hello");

        mockMvc.perform(get("/api/courses/" + courseId + "/quiz").header("Authorization", token))
            .andExpect(status().isBadRequest());
    }

    @Test
    void generateSubmitAndTrackProgress() throws Exception {
        String token = authToken();
        Long courseId = createCourse(token);
        Long word1 = addWord(token, courseId, "hola", "hello");
        Long word2 = addWord(token, courseId, "gato", "cat");
        addWord(token, courseId, "perro", "dog");
        addWord(token, courseId, "adios", "goodbye");

        // generate a quiz and check shape
        String quizBody = mockMvc.perform(get("/api/courses/" + courseId + "/quiz?size=3")
                .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.questions.length()").value(3))
            .andReturn().getResponse().getContentAsString();

        JSONArray questions = JsonPath.read(quizBody, "$.questions");
        assertThat(questions).hasSize(3);
        for (int i = 0; i < questions.size(); i++) {
            List<String> options = JsonPath.read(quizBody, "$.questions[" + i + "].options");
            assertThat(options).hasSizeBetween(2, 4);
        }

        // answer every question correctly (word meanings are known from setup)
        String submission = """
            {"answers":[
                {"wordId":%d,"selectedMeaning":"hello"},
                {"wordId":%d,"selectedMeaning":"cat"}
            ]}""".formatted(word1, word2);

        String resultBody = mockMvc.perform(post("/api/courses/" + courseId + "/quiz/submit")
                .header("Authorization", token)
                .contentType("application/json")
                .content(submission))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score").value(2))
            .andExpect(jsonPath("$.total").value(2))
            .andReturn().getResponse().getContentAsString();
        assertThat((Boolean) JsonPath.read(resultBody, "$.results[0].correct")).isTrue();

        // answer one wrong to confirm grading marks it incorrect and lowers the score
        String wrongSubmission = """
            {"answers":[
                {"wordId":%d,"selectedMeaning":"cat"}
            ]}""".formatted(word1);

        mockMvc.perform(post("/api/courses/" + courseId + "/quiz/submit")
                .header("Authorization", token)
                .contentType("application/json")
                .content(wrongSubmission))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.score").value(0))
            .andExpect(jsonPath("$.results[0].correct").value(false));

        // progress history should now contain both attempts, most recent first
        mockMvc.perform(get("/api/courses/" + courseId + "/progress").header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].score").value(0))
            .andExpect(jsonPath("$[1].score").value(2));
    }

    @Test
    void submitRejectsWordFromAnotherCourse() throws Exception {
        String token = authToken();
        Long courseId = createCourse(token);
        addWord(token, courseId, "hola", "hello");
        addWord(token, courseId, "gato", "cat");

        Long otherCourseId = createCourse(token);
        Long otherWordId = addWord(token, otherCourseId, "chien", "dog");
        addWord(token, otherCourseId, "chat", "cat");

        String submission = """
            {"answers":[{"wordId":%d,"selectedMeaning":"dog"}]}""".formatted(otherWordId);

        mockMvc.perform(post("/api/courses/" + courseId + "/quiz/submit")
                .header("Authorization", token)
                .contentType("application/json")
                .content(submission))
            .andExpect(status().isBadRequest());
    }
}
