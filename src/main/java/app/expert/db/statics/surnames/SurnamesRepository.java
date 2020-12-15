package app.expert.db.statics.surnames;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurnamesRepository extends JpaRepository<Surname,Long> {
    Page<Surname> findAllByNameStartingWithIgnoreCaseOrderByNameCountDesc(String name, Pageable pageable);
}

