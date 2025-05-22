package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.User;
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

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
