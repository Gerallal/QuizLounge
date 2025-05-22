package de.thb.quizlounge.controller;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String repeatedPassword, Model model){

        if(password.equals(repeatedPassword)){
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            System.out.println("in der Scheife");
            userService.save(user);
            return "redirect:/login";


        }
        System.out.println("außerhalb der Scheife");
        return "fail";
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
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        System.out.println(username);
        User currentUser = userService.getUserByName(username);
        if(currentUser == null) {
            return "login";
        }
        if(currentUser.getPassword().equals(password)) {
            model.addAttribute("user", currentUser);

            session.setAttribute("user", currentUser);
            return "redirect:/home";

        }
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model){
        return "home";
    }
}
