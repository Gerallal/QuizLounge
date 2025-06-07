package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.entity.User;
import de.thb.quizlounge.repository.QuizRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.hibernate.Hibernate;


import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;

    public List<Quiz> getAllQuizzes() {
        return (List<Quiz>) quizRepository.findAll();
    }

    public Optional<Quiz> getQuizById(long id) {
        return quizRepository.findById(id);
    }

    public Quiz getQuizWithDetails(long id) {
        Quiz quiz = quizRepository.findById(id).orElseThrow();
        Hibernate.initialize(quiz.getAttempts());
        Hibernate.initialize(quiz.getQuestions());
        return quiz;
    }

    public void saveQuiz(Quiz quiz) {
        quizRepository.save(quiz);
    }

    public void deleteQuizById(long id) {
        quizRepository.deleteById(id);
    }

    public void updateQuiz(long id, Quiz updatedQuiz) {
        Quiz existing = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz nicht gefunden"));
        existing.setTitle(updatedQuiz.getTitle());
        existing.setDescription(updatedQuiz.getDescription());
        quizRepository.save(existing);
    }
    public List<Quiz> getQuizzesByAuthor(User author) {
        return quizRepository.findByAuthor(author);
    }


}
