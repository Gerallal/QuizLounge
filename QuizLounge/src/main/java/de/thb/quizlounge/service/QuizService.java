package de.thb.quizlounge.service;

import de.thb.quizlounge.repository.QuestionRepository;
import de.thb.quizlounge.repository.QuizRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;

}
