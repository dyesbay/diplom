package app.expert.db.statics.manager_role_route;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagerRoleRouteRepository extends JpaRepository<ManagerRoleRoute, ManagerRoleRouteKey> {

    List<ManagerRoleRoute> findByManagerRoleCode(String code);

    List<ManagerRoleRoute> findByRouteId(Long id);
}
