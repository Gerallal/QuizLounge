package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.FriendRequest;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.repository.FriendRequestRepository;
import de.thb.quizlounge.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import javax.persistence.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService{

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public void save(User user){
        userRepository.save(user);
    }

    public Optional<User> getUserById(long id){
        return userRepository.findById(id);
    }

    public User getUserByName(String name){ return userRepository.findByUsername(name); }

    public List<FriendRequest> getFriendRequestsByUser(User user){ return friendRequestRepository.findByReceiver(user); }

    public void saveRequest(FriendRequest request){ friendRequestRepository.save(request); }

    public Optional<FriendRequest> getRequestById(long id){ return friendRequestRepository.findById(id); }

    public void deleteFriendRequest(FriendRequest request){ friendRequestRepository.delete(request); }

    public void deleteRequestById(Long id){ friendRequestRepository.deleteById(id); }

    public void acceptFriendRequest(FriendRequest friendRequest){
        friendRequest.setAccepted(true);

        User sender = friendRequest.getSender();
        User receiver = friendRequest.getReceiver();

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        userRepository.save(sender);
        userRepository.save(receiver);
        friendRequestRepository.delete(friendRequest);
    }

    public void declineFriendRequest(FriendRequest friendRequest){ friendRequestRepository.delete(friendRequest); }



}
