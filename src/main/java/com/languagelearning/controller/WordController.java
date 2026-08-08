package com.languagelearning.controller;

import com.languagelearning.dto.WordProgressRequest;
import com.languagelearning.dto.WordProgressResponse;
import com.languagelearning.dto.WordRequest;
import com.languagelearning.dto.WordResponse;
import com.languagelearning.model.User;
import com.languagelearning.service.WordProgressService;
import com.languagelearning.service.WordService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WordController {

    private final WordService wordService;
    private final WordProgressService wordProgressService;

    public WordController(WordService wordService, WordProgressService wordProgressService) {
        this.wordService = wordService;
        this.wordProgressService = wordProgressService;
    }

    @PostMapping("/api/courses/{courseId}/words")
    public ResponseEntity<WordResponse> create(@PathVariable Long courseId, @Valid @RequestBody WordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wordService.create(courseId, request));
    }

    @GetMapping("/api/courses/{courseId}/words")
    public Page<WordResponse> listByCourse(@PathVariable Long courseId, Pageable pageable) {
        return wordService.listByCourse(courseId, pageable);
    }

    @GetMapping("/api/words/{id}")
    public WordResponse get(@PathVariable Long id) {
        return wordService.get(id);
    }

    @PutMapping("/api/words/{id}")
    public WordResponse update(@PathVariable Long id, @Valid @RequestBody WordRequest request) {
        return wordService.update(id, request);
    }

    @DeleteMapping("/api/words/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        wordService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/words/{id}/progress")
    public WordProgressResponse review(@PathVariable Long id, @Valid @RequestBody WordProgressRequest request,
                                        @AuthenticationPrincipal User user) {
        return wordProgressService.review(id, user, request);
    }

    @GetMapping("/api/courses/{courseId}/word-progress")
    public Page<WordProgressResponse> progressByCourse(@PathVariable Long courseId,
                                                         @AuthenticationPrincipal User user, Pageable pageable) {
        return wordProgressService.listByCourse(courseId, user, pageable);
    }
}
