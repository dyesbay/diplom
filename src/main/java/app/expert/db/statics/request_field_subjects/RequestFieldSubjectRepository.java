package app.expert.db.statics.request_field_subjects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestFieldSubjectRepository extends JpaRepository<RequestFieldSubject, RqFieldSubjectKey> {

    List<RequestFieldSubject> findBySubject(Long id);

    RequestFieldSubject findByRequestField(Long id);

    RequestFieldSubject findByRequestFieldAndSubject(Long requestField, Long subject);
}
