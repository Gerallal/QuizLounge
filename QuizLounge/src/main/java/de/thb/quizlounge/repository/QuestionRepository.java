package de.thb.quizlounge.repository;

import de.thb.quizlounge.entity.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = Question.class, idClass = Long.class)
public interface QuestionRepository extends CrudRepository<Question, Long> {

}
