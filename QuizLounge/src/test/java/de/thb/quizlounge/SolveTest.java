package de.thb.quizlounge;

import de.thb.quizlounge.controller.AttemptController;
import de.thb.quizlounge.entity.Attempt;
import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.service.AttemptService;
import de.thb.quizlounge.service.QuizService;
import de.thb.quizlounge.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SolveTest {

    @Mock
    private UserService userService;
    @Mock
    private QuizService quizService;
    @Mock
    private AttemptService attemptService;
    @Mock
    private HttpSession session;
    @Mock
    private Model model;

    @InjectMocks
    private AttemptController attemptController;

    @Test
    public void solveNoUser() {
        //arrange
        when(session.getAttribute("user")).thenReturn(null);
        //act
        String result = attemptController.solve(42L, model, session);
        //assert
        assertEquals("redirect:/login", result);
        //verify
        verify(session).getAttribute("user");
    }

    @Test
    public void solveQuizNotFound() {
        //arrange
        User user = new User();
        user.setUsername("testusername");
        user.setPassword("testpassword");

        when(session.getAttribute("user")).thenReturn(user);
        when(userService.getUserByName(user.getUsername())).thenReturn(user);
        when(quizService.getQuizById(42L)).thenReturn(Optional.empty());

        //act
        String result = attemptController.solve(42L, model, session);
        //assert
        assertEquals("fail", result);
        //verify
        verify(session).getAttribute("user");
        verify(userService).getUserByName(user.getUsername());
        verify(quizService).getQuizById(42L);
    }

    @Test
    public void solveSuccess() {
        //arrange
        User user = new User();
        user.setUsername("testusername");
        user.setPassword("testpassword");
        Quiz quiz = new Quiz();
        Attempt attempt = new Attempt();
        attempt.setId(42L);

        when(session.getAttribute("user")).thenReturn(user);
        when(userService.getUserByName(user.getUsername())).thenReturn(user);
        when(quizService.getQuizById(42L)).thenReturn(Optional.of(quiz));
        when(attemptService.save(any(Attempt.class))).thenReturn(attempt);

        //act
        String result = attemptController.solve(42L, model, session);

        //assert
        assertEquals("redirect:/quizzes/solvequiz/42", result);
        //verify
        verify(session).getAttribute("user");
        verify(userService).getUserByName(user.getUsername());
        verify(quizService).getQuizById(42L);
        verify(attemptService).save(any(Attempt.class));
    }
}
