package app.expert.db.statics.manager_role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRoleRepository extends JpaRepository<ManagerRole, String> {
}
