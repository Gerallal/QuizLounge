package de.thb.quizlounge.controller;
import de.thb.quizlounge.entity.FriendRequest;
import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.service.QuizService;
import de.thb.quizlounge.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final QuizService quizService;

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
        model.addAttribute("statusText", "statusText");
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
    public String login(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        User currentUser = userService.getUserByName(username);
        if(currentUser == null) {
            return "login";
        }


        if(currentUser.getPassword().equals(password)) {
            model.addAttribute("user", currentUser);
            session.setAttribute("user", currentUser);
            session.setAttribute("userId", currentUser.getId());
            return "redirect:/home";
        }

        return "login";
    }


    @GetMapping("/home")
    public String home(Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/login";
        }
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
        if(currentUser == null) {
            return "redirect:/login";
        }
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
        if(sender == null) {
            return "redirect:/login";
        }
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

    @PostMapping("/home/friends/remove/{id}")
    public String removeFriend(@PathVariable("id") long friendId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId).orElse(null);
        if(user == null) {
            return "redirect:/login";
        }
        userService.deleteFriend(userId, friendId);
        return "redirect:/home/friends";
    }


    @PostMapping("/home/friends/accept")
    public String acceptFriendRequest(@RequestParam Long requestId, HttpSession session){
        FriendRequest friendRequest = userService.getRequestById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        // friendRequest.setAccepted(true);

        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "redirect:/login";
        }
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
    @GetMapping("/quizzes/send/{id}")
    public String send(@PathVariable Long id, Model model, HttpSession session) {

        User currentUser = (User) session.getAttribute("user");
        User newUser = userService.getUserByName(currentUser.getUsername());
        List <User> friends = newUser.getFriends();
        model.addAttribute("friends", friends);
        model.addAttribute("quizId", id);
        return "send";

    }

 /*   @GetMapping("/quizzes/transmit/{id}")
    public String sendQuiz(@PathVariable Long id, @RequestParam String username, Model model, HttpSession session) {
        if(username == null){
            return "fail";
        }


    }*/
    @PostMapping("/quizzes/transmit/{id}")
    public String transmitQuiz(
            @PathVariable Long id,
            @RequestParam String username,  // Pflichtfeld
            Model model) {
        // Logik...
        System.out.println(username);
        User victim = userService.getUserByName(username);
        Quiz quiz = quizService.getQuizById(id).orElse(null);
        if(!victim.getQuizes().contains(quiz)) {
            victim.getQuizes().add(quiz);

            userService.save(victim);
        } else return "fail";

        return "redirect:/home";
    }

}
