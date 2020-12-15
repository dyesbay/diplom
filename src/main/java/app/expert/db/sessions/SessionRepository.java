package app.expert.db.sessions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findFirstByManagerAndDisabledIsNull(Long manager);

    Session findByManager(Long manager);

    @Query(value = "select s.* from sessions s inner join managers m on s.manager = m.id WHERE m.username = :userName ;", nativeQuery = true)
    Optional<List<Session>> findByManagerUserName(@Param("userName") String userName);
}
