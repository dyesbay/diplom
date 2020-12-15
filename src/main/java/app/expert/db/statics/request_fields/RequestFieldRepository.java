package app.expert.db.statics.request_fields;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestFieldRepository extends JpaRepository<RequestField, Long> {
}
