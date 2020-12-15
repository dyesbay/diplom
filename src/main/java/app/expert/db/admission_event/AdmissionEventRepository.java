package app.expert.db.admission_event;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionEventRepository extends JpaRepository<AdmissionEvent, Long> {

    List<AdmissionEvent> findByAdmission(Long id);

    List<AdmissionEvent> findByAdmission(Sort sort, Long id);
}
