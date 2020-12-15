package app.expert.db.statics.message_tmpl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MassageTmplReposiroty extends JpaRepository<MessegeTmpl, Long> {
}
