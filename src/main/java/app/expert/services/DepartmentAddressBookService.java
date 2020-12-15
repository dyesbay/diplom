package app.expert.services;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.models.GFilter;
import app.expert.db.statics.department_address_book.DepartmentAddressBook;
import app.expert.db.statics.department_address_book.DepartmentAddressBookCache;
import app.expert.models.department_address_book.RqEditDepartmentAddress;
import app.expert.models.department_address_book.RsDepartmentAddressBook;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DepartmentAddressBookService {

    private final DepartmentAddressBookCache cache;

    public RsDepartmentAddressBook edit(RqEditDepartmentAddress request) throws GNotAllowed, GNotFound {

        DepartmentAddressBook entity = cache.find(request.getId());
        entity.edit(request);

        return RsDepartmentAddressBook.getFromEntity(cache.save(entity));
    }

    public Page<RsDepartmentAddressBook> getFilteredPage(GFilter<Long, DepartmentAddressBook> rq) {
        return cache.getRepository().findAll(rq, new PageRequest(rq.getPage(), rq.getSize()))
                .map(RsDepartmentAddressBook::getFromEntity);
    }
}
