package de.thb.quizlounge.controller;

import de.thb.quizlounge.entity.Attempt;
import de.thb.quizlounge.service.AttemptService;
import de.thb.quizlounge.service.QuizService;
import de.thb.quizlounge.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/quizzes")
public class AttemptController {
    private final QuizService quizService;
    private final AttemptService attemptService;
    private final UserService userService;


    @GetMapping("solve/{id}")
    public String solve(@PathVariable long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if(user == null) {
            return "redirect:/login";
        }
        user = userService.getUserByName(user.getUsername());
        Quiz quiz = quizService.getQuizById(id).orElse(null);
        if(quiz == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No quiz available");

        }
        if (quiz.getQuestions().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No questions available");
        }
        Attempt attempt = attemptService.startAttempt(quiz, user);
        return "redirect:/quizzes/solvequiz/" + attempt.getId();
    }

    @GetMapping("solvequiz/{id}")
    public String solveQuiz(@PathVariable long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if(user == null) {
            return "redirect:/login";
        }

        user = userService.getUserByName(user.getUsername());
        Attempt attempt = attemptService.findAttemptById(id).orElse(null);
        if(attempt.isFinished()) {
            return "redirect:/home";
        }
        if((user != attempt.getUser())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not belong to Attempt."); }

        Quiz quiz = attempt.getQuiz();

        if(quiz.getQuestions().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No questions available");
        }
        model.addAttribute("quiz", quiz);
        model.addAttribute("attempt", attempt);
        return "solve_quiz";
    }

    @PostMapping("solvequiz/{id}")
    public String evaluateAttempt(@PathVariable long id, Model model, @RequestParam Map<String,String> allParams, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Attempt attempt = attemptService.findAttemptById(id).orElse(null);
        if(allParams == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no answers send");
        }

        attemptService.evaluateAttempt(attempt, allParams);
        Quiz quiz = attempt.getQuiz();
        quiz.getAttempts().add(attempt);
        quizService.saveQuiz(quiz);
        model.addAttribute("attempt", attempt);
        return "attempt_finished";
    }

    @GetMapping("attempts/{id}")
    public String getAttempt(@PathVariable long id, Model model, HttpSession session) {
        Quiz quiz = quizService.getQuizById(id).orElse(null);
        List <Attempt> attempts = quiz.getAttempts();
        attempts.sort(null);
        Collections.reverse(attempts);
        model.addAttribute("attempts", attempts);
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "attempts_table";
    }

    @PostMapping("/feedback/{id}")
    public String submitFeedback(@PathVariable long id, @RequestParam Long quizId, @RequestParam Integer rating, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Optional<Attempt> optionalAttempt = attemptService.getLatestAttemptByUserAndQuiz(user.getId(), quizId);
        if (optionalAttempt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "attempt not found");
        }
        if(rating.compareTo(5) > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rating is too high");

        }

        Attempt attempt = optionalAttempt.get();

        attemptService.save(attempt);

        Quiz quiz = attempt.getQuiz();
        quiz.setTotalRating(quiz.getTotalRating() + rating);
        quiz.setNumberOfRatings(quiz.getNumberOfRatings() + 1);
        quizService.saveQuiz(quiz);

        return "redirect:/quizzes/attempts/" + id;
    }
}
