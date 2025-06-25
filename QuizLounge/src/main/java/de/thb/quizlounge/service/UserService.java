package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService{
    private final UserRepository userRepository;

    public void save(User user){
        userRepository.save(user);
    }

    public Optional<User> getUserById(long id){
        return userRepository.findById(id);
    }

    public User getUserByName(String name) {
        return userRepository.findByUsername(name)
                .orElse(null);
    }
}
