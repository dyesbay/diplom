package app.expert.db.manager_address_book;

import app.base.db.GRedisCache;
import org.springframework.stereotype.Component;

@Component
public class ManagerAddressBookCache extends GRedisCache<Long, ManagerAddressBook, ManagerAddressBookRepository> {

    public ManagerAddressBookCache(ManagerAddressBookRepository repository) {
        super(repository, ManagerAddressBook.class);
    }
}
