package de.thb.quizlounge.controller;

import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.service.FriendRequestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    private final FriendRequestService friendRequestService;

    @PostMapping("/add")
    public String addFriend(@RequestParam String username, HttpSession session) {
        User sender = (User) session.getAttribute("user");

        if (sender == null) {
            return "redirect:/login"; // Falls nicht eingeloggt
        }
        friendRequestService.sendFriendRequest(username, sender);
        return "redirect:/home"; // Weiterleitung zur home.html
    }


    @GetMapping
    public String showFriendsPage() {
        return "friends";
    }
}
