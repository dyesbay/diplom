package app.expert.services;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.models.GFilter;
import app.expert.db.manager_address_book.ManagerAddressBook;
import app.expert.db.manager_address_book.ManagerAddressBookCache;
import app.expert.models.manager_address_book.RqEditManagerAddress;
import app.expert.models.manager_address_book.RsManagerAddressBook;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerAddressBookService {

    private final ManagerAddressBookCache cache;

    public RsManagerAddressBook edit(RqEditManagerAddress request) throws GNotAllowed, GNotFound {

        ManagerAddressBook entity = cache.find(request.getId());
        entity.edit(request);

        return RsManagerAddressBook.getFromEntity(cache.save(entity));
    }

    public Page<RsManagerAddressBook> getFilteredPage(GFilter<Long, ManagerAddressBook> rq) {
        return cache.getRepository().findAll(rq, new PageRequest(rq.getPage(), rq.getSize()))
                .map(RsManagerAddressBook::getFromEntity);
    }
}
