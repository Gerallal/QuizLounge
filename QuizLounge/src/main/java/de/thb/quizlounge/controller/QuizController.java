package de.thb.quizlounge.controller;

import de.thb.quizlounge.entity.Attempt;
import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.service.AttemptService;
import de.thb.quizlounge.service.QuizService;
import de.thb.quizlounge.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
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
public class QuizController {
    private final QuizService quizService;
    private final UserService userService;
    private final AttemptService attemptService;

    @GetMapping("")
    public String showQuizzes(Model model, HttpSession session) {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        model.addAttribute("quizzes", quizzes);
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "quizzes";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        model.addAttribute("quiz", new Quiz());
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "create_quiz";
    }

    @GetMapping("/{id}")
    public String showQuizDetailsPublic(@PathVariable("id") long id, Model model, HttpSession session) {
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("quiz", quiz);

        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "questions";
    }

    @GetMapping("/my/{id}")
    public String showQuizDetailsPrivate(@PathVariable("id") long id, Model model, HttpSession session) {
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("quiz", quiz);

        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "my_questions";
    }

    @PostMapping("/create")
    public String createQuiz(@ModelAttribute Quiz quiz, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }

        quiz.setAuthor(currentUser);
        quizService.saveQuiz(quiz);
        return "redirect:/quizzes/my";
    }


    @PostMapping("/delete/{id}")
    public String deleteQuiz(@PathVariable long id, HttpSession session) {
        quizService.deleteQuizById(id);
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "redirect:/quizzes/my";
    }

    // Zeigt das Bearbeitungsformular
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable long id, Model model, HttpSession session) {
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("quiz", quiz);
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "edit_quiz";
    }

    @PostMapping("/edit/{id}")
    public String updateQuiz(@PathVariable long id, @ModelAttribute Quiz quiz, HttpSession session) {
        quizService.updateQuiz(id, quiz);
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "redirect:/quizzes/my";
    }

    @GetMapping("/my")
    public String getMyQuizzes(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user"); // oder SecurityContext, falls du Spring Security nutzt

        if(user == null) {
            return "redirect:/login";
        }

        List<Quiz> quizzes = quizService.getQuizzesByAuthor(user);
        model.addAttribute("quizzes", quizzes);

        return "my_quizzes";
    }

    @GetMapping("/ql")
    public String getQuizzes(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user"); // oder SecurityContext, falls du Spring Security nutzt

        if(user == null) {
            return "redirect:/login";
        }
        user = userService.getUserByName(user.getUsername());
        List<Quiz> quizzes = user.getQuizes();
        model.addAttribute("quizzes", quizzes);

        List<Quiz> myQuizzes = quizService.getQuizzesByAuthor(user);
        model.addAttribute("myQuizzes", myQuizzes);

        return "ql";
    }

    @GetMapping("solve/{id}")
    public String solve(@PathVariable long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/login";
        }
        user = userService.getUserByName(user.getUsername());
        Quiz quiz = quizService.getQuizById(id).orElse(null);
        if(quiz == null) {
            return "fail";
        }
        Attempt attempt = new Attempt();
        attempt.setQuiz(quiz);
        attempt.setUser(user);
        attempt.setFinished(false);
        attempt.setStartTime();
        attempt = attemptService.save(attempt);
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
        if(attempt == null || (user != attempt.getUser())) { return "fail"; }

        Quiz quiz = attempt.getQuiz();

        if(quiz.getQuestions().isEmpty()) {
            return "fail";
        }
        model.addAttribute("quiz", quiz);
        return "solve_quiz";
    }

    @PostMapping("solvequiz/{id}")
    public String updateFoos(@PathVariable long id, Model model, @RequestParam Map<String,String> allParams, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Attempt attempt = attemptService.findAttemptById(id).orElse(null);
        if(allParams == null){return "fail";}
        /*attempt.evaluate(allParams);
        attempt.setFinished(true);
        attempt.setEndTime();
        attempt.getDuration();
        attemptService.save(attempt);*/
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

        Attempt attempt = optionalAttempt.get();
        //attempt.setRating(rating);
        attemptService.save(attempt);

        Quiz quiz = attempt.getQuiz();
        quiz.setTotalRating(quiz.getTotalRating() + rating);
        quiz.setNumberOfRatings(quiz.getNumberOfRatings() + 1);
        quizService.saveQuiz(quiz);

        return "redirect:/quizzes/attempts/" + id;
    }


}
