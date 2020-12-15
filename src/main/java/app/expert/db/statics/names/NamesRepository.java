package app.expert.db.statics.names;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NamesRepository extends JpaRepository<Name,Long> {
    Page<Name> findAllByNameStartingWithIgnoreCaseOrderByNameCountDesc(String name, Pageable pageable);
}
