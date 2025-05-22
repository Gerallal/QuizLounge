package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.repository.QuestionRepository;
import de.thb.quizlounge.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

}
