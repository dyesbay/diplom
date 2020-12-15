package app.expert.db.managers_notifications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ManagersNotificationsRepository extends JpaRepository<ManagerNotification, Long> {

    List<ManagerNotification> findAllByRole(String role);

    @Query("select a from ManagerNotification a where a.notification <= :end and a.notification >= :start and a.role = :role and a.disabled = null")
    List<ManagerNotification> findAllByRoleAndNotificationGreaterThanOrEqualToAndNotificationLessThanOrEqualTo(
            @Param("role") String role, @Param("start")Date start, @Param("end")Date end);

    @Query("select a from ManagerNotification a where a.notification >= :start and a.role = :role and a.disabled = null")
    List<ManagerNotification> findAllByRoleAndNotificationOnGreaterThanOrEqualTo(@Param("role") String role, @Param("start")Date start);

    @Query("select a from ManagerNotification a where a.notification <= :end and a.role = :role and a.disabled = null")
    List<ManagerNotification> findAllByRoleAndNotificationOnLessThanOrEqualTo(@Param("role")String role, @Param("end")Date end);
}
