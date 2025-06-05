package de.thb.quizlounge.controller;

import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.service.QuizService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/quizzes")
public class QuizController {
    private final QuizService quizService;

    @GetMapping("")
    public String showQuizzes(Model model) {
        List<Quiz> quizzes = quizService.getAllQuizzes();
        model.addAttribute("quizzes", quizzes);
        return "quizzes";
    }

    /**
     * http://localhost:8080/notebooks/2
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("quiz", new Quiz());
        return "create_quiz";
    }

    @GetMapping("/{id}")
    public String showQuizDetailsPublic(@PathVariable("id") long id,
                                      Model model) {
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("quiz", quiz);

        return "questions";
    }

    @GetMapping("/my/{id}")
    public String showQuizDetailsPrivate(@PathVariable("id") long id,
                                     Model model) {
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        model.addAttribute("quiz", quiz);

        return "my_questions";
    }

    @PostMapping("/create")
    public String createQuiz(@ModelAttribute Quiz quiz, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            // nicht eingeloggt → zurück zur Login-Seite oder Fehlerseite
            return "redirect:/login";
        }

        quiz.setAuthor(currentUser);
        quizService.saveQuiz(quiz);
        return "redirect:/quizzes/my";
    }


    @PostMapping("/delete/{id}")
    public String deleteQuiz(@PathVariable long id) {
        quizService.deleteQuizById(id);
        return "redirect:/quizzes/my";
    }

    // Zeigt das Bearbeitungsformular
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable long id, Model model) {
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("quiz", quiz);
        return "edit_quiz";
    }

    @PostMapping("/edit/{id}")
    public String updateQuiz(@PathVariable long id, @ModelAttribute Quiz quiz) {
        quizService.updateQuiz(id, quiz);
        return "redirect:/quizzes/my";
    }

    @GetMapping("/my")
    public String getMyQuizzes(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user"); // oder SecurityContext, falls du Spring Security nutzt

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        List<Quiz> quizzes = quizService.getQuizzesByAuthor(user);
        model.addAttribute("quizzes", quizzes);

        return "my_quizzes"; // Name deiner Thymeleaf-View-Datei
    }

}
