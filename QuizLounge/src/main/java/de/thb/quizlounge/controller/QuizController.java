package de.thb.quizlounge.controller;

import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.service.QuizService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @GetMapping("/addQuiz")
    public String addQuiz(Model model){
        return "quizzes";
    }

    @GetMapping("/addQuiz/addQuestion")
    public String addQuestion(Model model){
        return "question";
    }
    @PostMapping("/addQuiz/addQuestion")
    public String addQuestion(@RequestParam String addQuestion, Model model) {
        return "redirect:/addQuiz/addQuestion";
    }

}
