package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.Attempt;
import de.thb.quizlounge.entity.Question;
import de.thb.quizlounge.repository.AttemptRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
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
    public void evaluateAttempt(Attempt attempt, Map<String,String> answers) {
        //answers.get(quiz.getQuestions().get(0).getQuestionname());
        int correctAnswers = 0;
        for (Question question : attempt.getQuiz().getQuestions()) {
            if (answers.get(question.getQuestionname()) != null) {
                if (answers.get(question.getQuestionname()).equals(question.getRightAnswer())) {
                    correctAnswers++;
                }
            }
        }
        attempt.setNumberOfRightAnswers(correctAnswers);
        attempt.setFinished(true);
        attempt.setEndTime();
        attempt.getDuration();
        attemptRepository.save(attempt);
    }
}
