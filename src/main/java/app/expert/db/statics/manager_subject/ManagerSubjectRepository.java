package app.expert.db.statics.manager_subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerSubjectRepository extends JpaRepository<ManagerSubject, ManagerSubjectKey> {

    List<ManagerSubject> findAllBySubject(Long subject);
}
