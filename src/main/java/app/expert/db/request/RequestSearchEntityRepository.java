package app.expert.db.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestSearchEntityRepository extends JpaRepository<RequestSearchEntity, Long> {

    Page<RequestSearchEntity> findAll(Specification<RequestSearchEntity> spec, Pageable pageable);

    List<RequestSearchEntity> findAll(Specification<RequestSearchEntity> spec);

    Integer count(Specification<RequestSearchEntity> spec);
}
