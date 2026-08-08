package com.languagelearning.service;

import com.languagelearning.dto.QuizAnswerRequest;
import com.languagelearning.dto.QuizAnswerResult;
import com.languagelearning.dto.QuizAttemptSummaryResponse;
import com.languagelearning.dto.QuizQuestionResponse;
import com.languagelearning.dto.QuizResponse;
import com.languagelearning.dto.QuizResultResponse;
import com.languagelearning.dto.QuizSubmissionRequest;
import com.languagelearning.model.Course;
import com.languagelearning.model.QuizAttempt;
import com.languagelearning.model.User;
import com.languagelearning.model.Word;
import com.languagelearning.repository.CourseRepository;
import com.languagelearning.repository.QuizAttemptRepository;
import com.languagelearning.repository.WordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private static final int DEFAULT_SIZE = 5;
    private static final int MAX_OPTIONS = 4;

    private final CourseRepository courseRepository;
    private final WordRepository wordRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final Random random = new Random();

    public QuizService(CourseRepository courseRepository, WordRepository wordRepository,
                        QuizAttemptRepository quizAttemptRepository) {
        this.courseRepository = courseRepository;
        this.wordRepository = wordRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    public QuizResponse generateQuiz(Long courseId, Integer size) {
        Course course = findCourseOrThrow(courseId);
        List<Word> words = wordRepository.findByCourseId(courseId);
        if (words.size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "課程單字數不足，至少需要 2 個單字才能出題");
        }

        List<Word> shuffled = new ArrayList<>(words);
        Collections.shuffle(shuffled, random);
        int questionCount = Math.min(size == null ? DEFAULT_SIZE : size, shuffled.size());
        List<Word> selected = shuffled.subList(0, questionCount);

        List<QuizQuestionResponse> questions = selected.stream()
            .map(word -> new QuizQuestionResponse(word.getId(), word.getTerm(), buildOptions(word, words)))
            .toList();

        return new QuizResponse(course.getId(), questions);
    }

    public QuizResultResponse submit(Long courseId, QuizSubmissionRequest request, User user) {
        findCourseOrThrow(courseId);

        List<QuizAnswerResult> results = new ArrayList<>();
        int score = 0;
        for (QuizAnswerRequest answer : request.answers()) {
            Word word = wordRepository.findById(answer.wordId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Word not found: " + answer.wordId()));
            if (!word.getCourse().getId().equals(courseId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Word " + word.getId() + " does not belong to course " + courseId);
            }
            boolean correct = word.getMeaning().trim().equalsIgnoreCase(answer.selectedMeaning().trim());
            if (correct) {
                score++;
            }
            results.add(new QuizAnswerResult(word.getId(), word.getTerm(), word.getMeaning(),
                answer.selectedMeaning(), correct));
        }

        QuizAttempt attempt = quizAttemptRepository.save(
            new QuizAttempt(user, courseRepository.getReferenceById(courseId), score, results.size()));

        return new QuizResultResponse(attempt.getId(), courseId, score, results.size(), results);
    }

    public Page<QuizAttemptSummaryResponse> history(Long courseId, User user, Pageable pageable) {
        findCourseOrThrow(courseId);
        return quizAttemptRepository.findByUserIdAndCourseIdOrderByCreatedAtDesc(user.getId(), courseId, pageable)
            .map(QuizAttemptSummaryResponse::from);
    }

    private List<String> buildOptions(Word correctWord, List<Word> allWords) {
        List<String> distractors = allWords.stream()
            .filter(w -> !w.getId().equals(correctWord.getId()))
            .map(Word::getMeaning)
            .distinct()
            .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(distractors, random);

        List<String> options = new ArrayList<>();
        options.add(correctWord.getMeaning());
        options.addAll(distractors.subList(0, Math.min(MAX_OPTIONS - 1, distractors.size())));
        Collections.shuffle(options, random);
        return options;
    }

    private Course findCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }
}
