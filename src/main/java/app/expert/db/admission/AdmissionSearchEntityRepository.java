package app.expert.db.admission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdmissionSearchEntityRepository extends JpaRepository<AdmissionSearchEntity, Long> {

    Page<AdmissionSearchEntity> findAll(Specification<AdmissionSearchEntity> spec, Pageable pageable);
    List<AdmissionSearchEntity> findAll(Specification<AdmissionSearchEntity> spec);
    Integer count(Specification<AdmissionSearchEntity> spec);
}
