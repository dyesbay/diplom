package app.expert.db.statics.department_address_book;

import app.base.db.GRedisCache;
import org.springframework.stereotype.Component;

@Component
public class DepartmentAddressBookCache extends GRedisCache<Long, DepartmentAddressBook, DepartmentAddressBookRepository> {

    public DepartmentAddressBookCache(DepartmentAddressBookRepository repository) {
        super(repository, DepartmentAddressBook.class);
    }
}
