package de.thb.quizlounge.repository;

import de.thb.quizlounge.entity.FriendRequest;
import de.thb.quizlounge.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FriendRequestRepository extends CrudRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiver(User receiver);

    Long id(Long id);
}
