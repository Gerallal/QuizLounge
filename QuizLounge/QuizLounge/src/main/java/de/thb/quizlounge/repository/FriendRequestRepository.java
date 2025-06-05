package de.thb.quizlounge.repository;

import de.thb.quizlounge.entity.FriendRequest;
import de.thb.quizlounge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiver(User receiver);

    Long id(Long id);
}
