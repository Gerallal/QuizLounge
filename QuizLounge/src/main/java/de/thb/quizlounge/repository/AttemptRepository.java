package de.thb.quizlounge.repository;

import de.thb.quizlounge.entity.Attempt;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = Attempt.class, idClass = Long.class)
public interface AttemptRepository extends CrudRepository<Attempt, Long> {
    Optional<Attempt> findTopByUserIdAndQuizIdOrderByEndTimeDesc(Long userId, Long quizId);


}
