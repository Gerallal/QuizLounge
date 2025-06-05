package de.thb.quizlounge.controller;
import de.thb.quizlounge.entity.FriendRequest;
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
import java.util.ArrayList;
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
            user.setFriends(new ArrayList<User>());
            userService.save(user);
            return "redirect:/login";


        }
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
    public String home(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "home";
    }

    @PostMapping("/home")
    public String home(Model model){
        return "home";
    }

    @GetMapping("/home/friends")
    public String showFriends(Model model, HttpSession session){
        User currentUser = (User) session.getAttribute("user");

        // Optional: neu aus DB laden, damit Session aktiv ist
        User managedUser = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FriendRequest> friendRequests = userService.getFriendRequestsByUser(managedUser);
        List<User> friends = managedUser.getFriends(); // jetzt funktioniert LAZY Loading

        model.addAttribute("user", managedUser);
        model.addAttribute("friendRequests", friendRequests);
        model.addAttribute("friends", friends);

        return "friends";
    }

    @PostMapping("/home/friends")
    public String sendFriendRequest(@RequestParam String username, Model model, HttpSession session){
        User sender = (User) session.getAttribute("user");
        User receiver = userService.getUserByName(username);

        if (receiver != null && !sender.equals(receiver) && !(sender.getUsername().equals(username))){
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setSender(sender);
            friendRequest.setReceiver(receiver);
            friendRequest.setAccepted(false);
            userService.saveRequest(friendRequest);
            userService.save(receiver);
        }

        model.addAttribute("friendRequests", sender.getFriendRequests());

        return "redirect:/home/friends";
    }

    @PostMapping("/home/friends/accept")
    public String acceptFriendRequest(@RequestParam Long requestId, HttpSession session){
        FriendRequest friendRequest = userService.getRequestById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        // friendRequest.setAccepted(true);

        User sender = friendRequest.getSender();
        User receiver = friendRequest.getReceiver();

        if(sender.getFriends().contains(receiver)){
            return "fail";
        }

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        userService.save(sender);
        userService.save(receiver);
        userService.deleteFriendRequest(friendRequest);

        return "redirect:/home/friends";
    }

    @PostMapping("/home/friends/decline")
    public String declineRequest(@RequestParam Long requestId){
        userService.deleteRequestById(requestId);
        return "redirect:/home/friends";
    }

}
