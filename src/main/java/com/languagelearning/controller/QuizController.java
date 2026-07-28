package com.languagelearning.controller;

import com.languagelearning.dto.QuizAttemptSummaryResponse;
import com.languagelearning.dto.QuizResponse;
import com.languagelearning.dto.QuizResultResponse;
import com.languagelearning.dto.QuizSubmissionRequest;
import com.languagelearning.model.User;
import com.languagelearning.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/api/courses/{courseId}/quiz")
    public QuizResponse generateQuiz(@PathVariable Long courseId,
                                      @RequestParam(required = false) Integer size) {
        return quizService.generateQuiz(courseId, size);
    }

    @PostMapping("/api/courses/{courseId}/quiz/submit")
    public QuizResultResponse submit(@PathVariable Long courseId,
                                      @Valid @RequestBody QuizSubmissionRequest request,
                                      @AuthenticationPrincipal User user) {
        return quizService.submit(courseId, request, user);
    }

    @GetMapping("/api/courses/{courseId}/progress")
    public List<QuizAttemptSummaryResponse> progress(@PathVariable Long courseId,
                                                       @AuthenticationPrincipal User user) {
        return quizService.history(courseId, user);
    }
}
