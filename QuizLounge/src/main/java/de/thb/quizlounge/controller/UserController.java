package de.thb.quizlounge.controller;

import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;


@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String repeatedPassword, Model model){
        if(password.equals(repeatedPassword)){
            if(userService.getUserByName(username) != null) {
                model.addAttribute("statusText", "statusText");
                return "register";
            }
            if(username.length() > 12){
                return "register";
            }
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setFriends(new ArrayList<User>());
            userService.save(user);
            return "redirect:/login";
        }
        model.addAttribute("statusText", "Passwort nicht identisch");
        return "register";
    }

    @GetMapping("register")
    public String register(Model model){
        return "register";
    }

    @GetMapping("login")
    public String logIn(Model model){
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model,
                        HttpSession session,
                        HttpServletRequest request) {

        // Alte Session beenden
        session.invalidate();

        // Neue Session anlegen
        HttpSession newSession = request.getSession(true);

        // Benutzerprüfung
        User currentUser = userService.getUserByName(username);
        if (currentUser == null) {
            return "login"; // Benutzer nicht gefunden
        }

        if (currentUser.getPassword().equals(password)) {
            // Neue Session verwenden!
            newSession.setAttribute("user", currentUser);
            newSession.setAttribute("userId", currentUser.getId());
            return "redirect:/home";
        }

        return "login"; // Passwort falsch
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");

        if(user == null) {
            return "redirect:/login";
        }

        User managedUser = userService.getUserById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<User> friends = managedUser.getFriends();

        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        return "home";
    }

    @PostMapping("/home")
    public String home(Model model){
        return "home";
    }

}
