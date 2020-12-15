package app.expert.db.statics.request_field_sub_subjects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RqFieldSubSubjectRepository extends JpaRepository<RequestFieldSubSubject, RequestFieldSubSubjectKey> {

    List<RequestFieldSubSubject> findBySubSubject(Long id);

    RequestFieldSubSubject findByRequestField(Long id);
}
