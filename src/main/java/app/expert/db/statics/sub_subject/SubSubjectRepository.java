package app.expert.db.statics.sub_subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubSubjectRepository extends JpaRepository<SubSubject, Long> {

    List<SubSubject> findBySubject(Long id);
}
