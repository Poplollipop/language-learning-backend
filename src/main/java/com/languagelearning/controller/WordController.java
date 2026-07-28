package com.languagelearning.controller;

import com.languagelearning.dto.WordRequest;
import com.languagelearning.dto.WordResponse;
import com.languagelearning.service.WordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WordController {

    private final WordService wordService;

    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @PostMapping("/api/courses/{courseId}/words")
    public ResponseEntity<WordResponse> create(@PathVariable Long courseId, @Valid @RequestBody WordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wordService.create(courseId, request));
    }

    @GetMapping("/api/courses/{courseId}/words")
    public List<WordResponse> listByCourse(@PathVariable Long courseId) {
        return wordService.listByCourse(courseId);
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
}
