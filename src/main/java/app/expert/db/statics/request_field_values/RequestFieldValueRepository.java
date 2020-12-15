package app.expert.db.statics.request_field_values;

import org.hibernate.annotations.SQLDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestFieldValueRepository extends JpaRepository<RequestFieldValue, Long>{

    List<RequestFieldValue> findByField(Long id);

    //@Query(value = "DELETE FROM request_field_values WHERE field= :field_id ;", nativeQuery = true)
    @SQLDelete(sql = "FROM request_field_values WHERE field= :field_id ;")
    void deleteByField(@Param("field_id") Long id);
}
