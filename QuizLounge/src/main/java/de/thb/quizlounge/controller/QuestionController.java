package de.thb.quizlounge.controller;

import de.thb.quizlounge.entity.Question;
import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.service.QuizService;
import de.thb.quizlounge.service.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/questions")
@AllArgsConstructor
public class QuestionController {
    private final QuestionService questionService;
    private final QuizService quizService;

    @PostMapping("/create/{quizId}")
    public String createQuiz(@PathVariable long quizId, @RequestParam String questionname, @RequestParam String answer1,
                             @RequestParam String answer2, @RequestParam String answer3, @RequestParam String answer4,
                             @RequestParam int rightAnswerValue, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Quiz quiz = quizService.getQuizById(quizId).orElseThrow();
        Question question = new Question();
        question.setQuestionname(questionname);
        question.setAnswer1(answer1);
        question.setAnswer2(answer2);
        question.setAnswer3(answer3);
        question.setAnswer4(answer4);
        if(rightAnswerValue > 0 && rightAnswerValue < 5) {
            switch (rightAnswerValue) {
                case 1: question.setRightAnswer(answer1); break;
                case 2: question.setRightAnswer(answer2); break;
                case 3: question.setRightAnswer(answer3); break;
                case 4: question.setRightAnswer(answer4); break;
            }
        }
        question.setQuiz(quiz);
        questionService.save(question);
        return "redirect:/quizzes/my/" + quizId;
    }

    @PostMapping("/delete/{questionId}")
    public String deleteQuestion(@PathVariable long questionId, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Question question = questionService.getQuestionById(questionId).orElseThrow();
        questionService.deleteQuestionById(questionId);
        return "redirect:/quizzes/my/" + question.getQuiz().getId();
    }

    @GetMapping("/edit/{questionId}")
    public String showEditQuestionForm(@PathVariable long questionId, Model model, HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Question question = questionService.getQuestionById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("question", question);
        return "edit_question";
    }

    @PostMapping("/edit/{questionId}")
    public String updateQuestion(@PathVariable long questionId,
                                 @ModelAttribute Question question,
                                 HttpSession session) {
        if(session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        Question fullQuestion = questionService.getQuestionById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        questionService.updateQuestion(questionId, question);

        return "redirect:/quizzes/my/" + fullQuestion.getQuiz().getId();
    }
    
}
