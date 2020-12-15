package app.expert.db.statics.request_comments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestCommentsRepository extends JpaRepository<RequestComment, Long> {

    List<RequestComment> findAllByRequest(Long request);
}
