package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.Attempt;
import de.thb.quizlounge.repository.AttemptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AttemptService {
    private final AttemptRepository attemptRepository;

    public Attempt save(Attempt attempt) {
        return attemptRepository.save(attempt);
    }

    public Optional<Attempt> findAttemptById(Long id) {
        return attemptRepository.findById(id);
    }

    public Optional<Attempt> getLatestAttemptByUserAndQuiz(Long userId, Long quizId) {
        return attemptRepository.findTopByUserIdAndQuizIdOrderByEndTimeDesc(userId, quizId);
    }

}
