package de.thb.quizlounge.controller;

import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.service.QuizService;
import de.thb.quizlounge.service.UserService;
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
    private final UserService userService;

    @GetMapping("")
    public String showQuizzes(@RequestParam(value = "category", required = false) String category, Model model, HttpSession session) {
        List<Quiz> quizzes;

        if(category != null && !category.isEmpty()) {
            quizzes = quizService.getQuizzesByCategory(category);
            model.addAttribute("selectedCategory", category);
        } else quizzes = quizService.getAllQuizzes();

        model.addAttribute("quizzes", quizzes);
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "quizzes";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        model.addAttribute("quiz", new Quiz());
        return "create_quiz";
    }

    @GetMapping("/{id}")
    public String showQuizDetailsPublic(@PathVariable("id") long id, Model model, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("quiz", quiz);
        return "questions";
    }

    @GetMapping("/my/{id}")
    public String showQuizDetailsPrivate(@PathVariable("id") long id, Model model, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("quiz", quiz);
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
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        quizService.deleteQuizById(id);
        return "redirect:/quizzes/my";
    }

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
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        quizService.updateQuiz(id, quiz);
        return "redirect:/quizzes/my";
    }

    @GetMapping("/my")
    public String getMyQuizzes(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/login";
        }
        user = userService.getUserByName(user.getUsername());
        List<Quiz> quizzes = quizService.getQuizzesByAuthor(user);
        model.addAttribute("quizzes", quizzes);
        return "my_quizzes";
    }

    @GetMapping("/ql")
    public String getQuizzes(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
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

    @GetMapping("/send/{id}")
    public String send(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        User newUser = userService.getUserByName(currentUser.getUsername());
        List <User> friends = newUser.getFriends();
        model.addAttribute("friends", friends);
        model.addAttribute("quizId", id);
        return "send";
    }

    @PostMapping("/transmit/{id}")
    public String transmitQuiz(
            @PathVariable Long id,
            @RequestParam String username) {

        User receiver = userService.getUserByName(username);
        Quiz quiz = quizService.getQuizById(id).orElse(null);
        if(!receiver.getQuizes().contains(quiz)) {
            receiver.getQuizes().add(quiz);
            userService.save(receiver);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has Quiz.");
        return "redirect:/home";
    }
}
