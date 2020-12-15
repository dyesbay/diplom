package app.expert.db.statics.region;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionsRepository extends JpaRepository<Region, Long>, JpaSpecificationExecutor<Region> {
}
