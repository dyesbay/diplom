package app.expert.db.manager_address_book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerAddressBookRepository extends JpaRepository<ManagerAddressBook, Long>,
        JpaSpecificationExecutor<ManagerAddressBook> {
}
