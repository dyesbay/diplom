package app.expert.db.uploaded_files;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, String> {

    @Query(value = "SELECT name, size, uploaded_on FROM uploaded_files ; " , nativeQuery = true)
    List<Object[]> getFilesInfo();
}
