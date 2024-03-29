package com.elos.webquizengine.controller;

import com.elos.webquizengine.dto.AnswerDto;
import com.elos.webquizengine.model.Quiz;
import com.elos.webquizengine.model.User;
import com.elos.webquizengine.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"http://localhost:8080", "http://192.168.0.10:8080"})
@RequestMapping("/api")
public class QuizRestController {

    @Autowired
    private QuizService quizService;

    @PostMapping("/quizzes")
    public ResponseEntity<?> createNewQuiz(@Valid @RequestBody Quiz quiz, BindingResult errors, @AuthenticationPrincipal User user) {
        quiz.setAuthor(user);
        if (errors.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(quizService.add(quiz));
    }

    @GetMapping("/quizzes/{id}")
    public ResponseEntity<?> getQuiz(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getById(id));
    }

    @GetMapping("/quizzes")
    public ResponseEntity<?> getAllQuizzes(@RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(quizService.getAll(
                        PageRequest.of(page.orElse(0), size.orElse(10), Sort.by("id"))
                )
        );
    }

    @PostMapping("/quizzes/{id}/solve")
    public ResponseEntity<?> solveQuiz(@RequestBody(required = false) AnswerDto answerDto, @PathVariable Long id, @AuthenticationPrincipal User user) {
        System.out.println(answerDto.getAnswer());
        return ResponseEntity.ok(quizService.solveQuiz(id, answerDto, user));
    }

    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id, @AuthenticationPrincipal User user) {
        quizService.remove(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/quizzes/completed")
    public ResponseEntity<?> getCompletedQuizzesUser(@AuthenticationPrincipal User user, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(quizService.getAllByUser(user,
                PageRequest.of(page.orElse(0), size.orElse(10), Sort.by("id"))));
    }

    @GetMapping("/quizzes/chart")
    public ResponseEntity<?> getQuizzesForChart() {
        return ResponseEntity.ok(quizService.getQuizzesIn30Days());
    }
}
