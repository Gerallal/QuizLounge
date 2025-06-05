package de.thb.quizlounge.repository;

import de.thb.quizlounge.entity.FriendRequest;
import de.thb.quizlounge.entity.User;
import java.util.List;

    List<FriendRequest> findByReceiver(User receiver);

    Long id(Long id);
}
