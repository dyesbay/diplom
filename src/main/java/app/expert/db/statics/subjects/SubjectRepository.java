package app.expert.db.statics.subjects;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long>{

    List<Subject> findBySection(Long id);
}
