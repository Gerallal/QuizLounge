package de.thb.quizlounge;

import de.thb.quizlounge.controller.AttemptController;
import de.thb.quizlounge.entity.Attempt;
import de.thb.quizlounge.entity.Question;
import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.repository.AttemptRepository;
import de.thb.quizlounge.service.AttemptService;
import de.thb.quizlounge.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SolveQuizTest {
    @Mock
    private UserService userService;
    @Mock
    private AttemptRepository attemptRepository;
    @Spy
    private AttemptService attemptService = new AttemptService(attemptRepository);
    @Mock
    HttpSession session;
    @Mock
    Model model;
    @InjectMocks
    private AttemptController attemptController;

    @Test
    public void solveQuizSuccess() {
        //arrange
        User user = new User();
        user.setUsername("testusername");
        user.setPassword("testpassword");

        Quiz quiz = new Quiz();
        quiz.setId(42L);
        quiz.setQuestions(List.of(new Question()));

        Attempt attempt = new Attempt();
        attempt.setId(42L);
        attempt.setUser(user);
        attempt.setQuiz(quiz);

        when(session.getAttribute("user")).thenReturn(user);
        when(userService.getUserByName(user.getUsername())).thenReturn(user);
        //when(attemptService.findAttemptById(42L)).thenReturn(Optional.of(attempt));
        doReturn(Optional.of(attempt)).when(attemptService).findAttemptById(42L);
        //act
        String result = attemptController.solveQuiz(42L, model, session);
        //assert
        assertEquals("solve_quiz", result);
        //verify
        verify(session).getAttribute("user");
        verify(userService).getUserByName(user.getUsername());
        verify(attemptService).findAttemptById(42L);
        verify(model).addAttribute(eq("quiz"), any(Quiz.class));
    }

    @Test
    public void solveQuizNoUser() {
        //arrange
        when(session.getAttribute("user")).thenReturn(null);
        //act
        String result = attemptController.solveQuiz(42L, model, session);
        //assert
        assertEquals("redirect:/login", result);
        //verify
        verify(session).getAttribute("user");
    }

    @Test
    public void solveQuizNoQuestions() {
        //arrange
        User user = new User();
        user.setUsername("testusername");
        user.setPassword("testpassword");
        Attempt attempt = new Attempt();
        attempt.setId(42L);
        Quiz quiz = new Quiz();
        quiz.setId(42L);

        when(session.getAttribute("user")).thenReturn(user);
        when(userService.getUserByName(user.getUsername())).thenReturn(user);
        //when(attemptService.findAttemptById(42L)).thenReturn(Optional.of(attempt));
        doReturn(Optional.of(attempt)).when(attemptService).findAttemptById(42L);
        //act
        String result = attemptController.solveQuiz(42L, model, session);
        //assert
        assertEquals("fail", result);
        //verify
        verify(session).getAttribute("user");
        verify(userService).getUserByName(user.getUsername());
        verify(attemptService).findAttemptById(42L);
    }

    @Test
    public void solveQuizNoAttempts() {
        //arrange
        User user = new User();
        user.setUsername("testusername");
        user.setPassword("testpassword");
        Attempt attempt = new Attempt();

        when(session.getAttribute("user")).thenReturn(user);
        when(userService.getUserByName(user.getUsername())).thenReturn(user);
        //when(attemptService.findAttemptById(42L)).thenReturn(Optional.empty());
        doReturn(Optional.empty()).when(attemptService).findAttemptById(42L);
        //act
        String result = attemptController.solveQuiz(42L, model, session);
        //assert
        assertEquals("fail", result);
        //verify
        verify(session).getAttribute("user");
        verify(userService).getUserByName(user.getUsername());
        verify(attemptService).findAttemptById(42L);
    }


}