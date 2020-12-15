package app.expert.db.statics.region_phone_numbers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionPhoneNumberRepository extends JpaRepository<RegionPhoneNumber, Long>, JpaSpecificationExecutor<RegionPhoneNumber> {

    Optional<List<RegionPhoneNumber>> findByCode(Short code);
}
