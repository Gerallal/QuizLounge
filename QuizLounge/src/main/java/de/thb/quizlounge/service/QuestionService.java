package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.Question;
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

    public Optional<Question> getQuestionById(long id) {
        return questionRepository.findById(id);
    }

    public void updateQuestion(long id, Question updatedQuestion) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notiz nicht gefunden"));

        existingQuestion.setQuestionname(updatedQuestion.getQuestionname());
        existingQuestion.setAnswer1(updatedQuestion.getAnswer1());
        existingQuestion.setAnswer2(updatedQuestion.getAnswer2());
        existingQuestion.setAnswer3(updatedQuestion.getAnswer3());
        existingQuestion.setAnswer4(updatedQuestion.getAnswer4());
        existingQuestion.setRightAnswer(updatedQuestion.getRightAnswer());

        questionRepository.save(existingQuestion);
    }


    public void deleteQuestionById(long id) {
        questionRepository.deleteById(id);
    }

    public void save(Question question) {
        questionRepository.save(question);
    }
}
