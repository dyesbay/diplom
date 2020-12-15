package app.expert.db.statics.admission_event_type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdmissionEventTypeRepository extends JpaRepository<AdmissionEventType, String> {
}
