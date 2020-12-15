package app.expert.db.statics.department_address_book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentAddressBookRepository extends JpaRepository<DepartmentAddressBook, Long>,
        JpaSpecificationExecutor<DepartmentAddressBook> {
}
