package de.thb.quizlounge;

import de.thb.quizlounge.controller.UserController;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeTest {
    @Mock
    private HttpSession session;
    @Mock
    private Model model;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    @Captor
    ArgumentCaptor<User> userCaptor;
    @Captor
    ArgumentCaptor<List<User>> friendsCaptor;

    private User user; //deklarieren

    @BeforeEach
    void setUp() {
        user = new User(); //initialisiert
        user.setUsername("testusername");
        user.setPassword("testpassword");
    }
    @Test
    public void homeSuccess() {
        //arrange
        user.setId(42L);
        user.setFriends(List.of(new User()));

        when(session.getAttribute("user")).thenReturn(user);
        when(userService.getUserById(user.getId())).thenReturn(Optional.of(user));
        //act
        String result = userController.home(model, session);
        //assert
        assertEquals("home", result);
        //verify
        verify(session).getAttribute("user");
        verify(userService).getUserById(user.getId());
        verifyNoMoreInteractions(userService);
        //verify(model).addAttribute(eq("user"), any(User.class));
        //verify(model).addAttribute(eq("friends"), any(List.class));
        verify(model).addAttribute(eq("user"), userCaptor.capture());
        verify(model).addAttribute(eq("friends"), friendsCaptor.capture());

        assertEquals("testusername", userCaptor.getValue().getUsername());
        assertEquals(1, friendsCaptor.getValue().size());

    }

}
