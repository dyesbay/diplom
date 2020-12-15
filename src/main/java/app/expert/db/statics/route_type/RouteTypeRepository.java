package app.expert.db.statics.route_type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteTypeRepository extends JpaRepository<RouteType, String> {
}
