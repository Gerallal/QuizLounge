package de.thb.quizlounge.service;

import de.thb.quizlounge.entity.Question;
import de.thb.quizlounge.repository.QuestionRepository;
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

    public void updateQuestion(long id, Question updatedQuestion, int rightAnswerValue) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notiz nicht gefunden"));

        existingQuestion.setQuestionname(updatedQuestion.getQuestionname());
        existingQuestion.setAnswer1(updatedQuestion.getAnswer1());
        existingQuestion.setAnswer2(updatedQuestion.getAnswer2());
        existingQuestion.setAnswer3(updatedQuestion.getAnswer3());
        existingQuestion.setAnswer4(updatedQuestion.getAnswer4());
        if(rightAnswerValue > 0 && rightAnswerValue < 5) {
            switch (rightAnswerValue) {
                case 1: existingQuestion.setRightAnswer(existingQuestion.getAnswer1()); break;
                case 2: existingQuestion.setRightAnswer(existingQuestion.getAnswer2()); break;
                case 3: existingQuestion.setRightAnswer(existingQuestion.getAnswer3()); break;
                case 4: existingQuestion.setRightAnswer(existingQuestion.getAnswer4()); break;
            }
        }
        questionRepository.save(existingQuestion);
    }


    public void deleteQuestionById(long id) {
        questionRepository.deleteById(id);
    }

    public void save(Question question) {
        questionRepository.save(question);
    }
}
