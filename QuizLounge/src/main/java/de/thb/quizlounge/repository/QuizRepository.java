package de.thb.quizlounge.repository;

import de.thb.quizlounge.entity.Quiz;
import de.thb.quizlounge.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.List;

@RepositoryDefinition(domainClass = Quiz.class, idClass = Long.class)
public interface QuizRepository extends CrudRepository<Quiz, Long> {
    List<Quiz> findByAuthor(User user);
    List<Quiz> findByCategory(String category);

}
