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

import java.util.List;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String repeatedPassword, Model model){
        User user = new User();
        if(password.equals(repeatedPassword)){
            user.setUsername(username);
            user.setPassword(password);
            System.out.println("in der Scheife");
            return "register";


        }
        System.out.println("außerhalb der Scheife");
        return "fail";
    }
    @GetMapping("register")
    public String register(Model model){
        return "register";
    }
}
