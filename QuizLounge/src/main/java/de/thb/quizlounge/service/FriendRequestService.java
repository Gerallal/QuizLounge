package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.FriendRequest;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.repository.FriendRequestRepository;
import de.thb.quizlounge.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FriendRequestService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public List<FriendRequest> getFriendRequestsByUser(User user){
        return friendRequestRepository.findByReceiver(user);
    }

    public void saveRequest(FriendRequest request){
        friendRequestRepository.save(request);
    }

    public Optional<FriendRequest> getRequestById(long id){
        return friendRequestRepository.findById(id);
    }

    public void deleteFriendRequest(FriendRequest request){
        friendRequestRepository.delete(request);
    }

    public void deleteRequestById(Long id){
        friendRequestRepository.deleteById(id);
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();
        // Entferne Freundschaft in beide Richtungen
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);
        userRepository.save(user);
        userRepository.save(friend);
    }
}
