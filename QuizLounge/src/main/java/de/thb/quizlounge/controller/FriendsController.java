package de.thb.quizlounge.controller;

import de.thb.quizlounge.entity.FriendRequest;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.service.FriendRequestService;
import de.thb.quizlounge.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
@AllArgsConstructor
public class FriendsController {
    private final UserService userService;
    private final FriendRequestService friendRequestService;

    @GetMapping("/home/friends")
    public String showFriends(Model model, HttpSession session){
        User currentUser = (User) session.getAttribute("user");
        if(currentUser == null) {
            return "redirect:/login";
        }

        User managedUser = userService.getUserById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FriendRequest> friendRequests = friendRequestService.getFriendRequestsByUser(managedUser);
        List<User> friends = managedUser.getFriends();

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
        sender = userService.getUserByName(sender.getUsername());
        if(sender == null) {
            return "redirect:/login";
        }

        User receiver = userService.getUserByName(username);

        if(receiver.getFriends().contains(sender)) {
            throw new RuntimeException("Sender is already friend of receiver.");
        }
        if (!sender.getUsername().equals(username)){
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setSender(sender);
            friendRequest.setReceiver(receiver);
            friendRequest.setAccepted(false);
            friendRequestService.saveRequest(friendRequest);
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
        friendRequestService.deleteFriend(userId, friendId);
        return "redirect:/home/friends";
    }

    @PostMapping("/home/friends/accept")
    public String acceptFriendRequest(@RequestParam Long requestId, HttpSession session){
        FriendRequest friendRequest = friendRequestService.getRequestById(requestId)
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
        friendRequestService.deleteFriendRequest(friendRequest);
        return "redirect:/home/friends";
    }

    @PostMapping("/home/friends/decline")
    public String declineRequest(@RequestParam Long requestId){
        friendRequestService.deleteRequestById(requestId);
        return "redirect:/home/friends";
    }
}
