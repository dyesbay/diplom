package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.db.manager_address_book.ManagerAddressBook;
import app.expert.db.manager_address_book.ManagerAddressBookCache;
import app.expert.models.manager_address_book.ManagerAddressBookFilter;
import app.expert.models.manager_address_book.RqEditManagerAddress;
import app.expert.models.manager_address_book.RqManagerAddressBook;
import app.expert.models.manager_address_book.RsManagerAddressBook;
import app.expert.services.ManagerAddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Api(tags = "3004. Адресная книга сотрудников")
public class ManagersAddressBook extends GControllerAdvice {

    private final ManagerAddressBookService service;
    private final ManagerAddressBookCache cache;

    @PutMapping("/managerAddressBook")
    @ApiOperation("Добавить орган в адрсеную книгу")
    public RsManagerAddressBook add(@Validated @RequestBody RqManagerAddressBook request) {

        return RsManagerAddressBook.getFromEntity
                (cache.save(ManagerAddressBook.getFromRequest(request)));
    }

    @PatchMapping("/managerAddressBook")
    @ApiOperation("Редактировать орган")
    public RsManagerAddressBook edit(@Validated @RequestBody RqEditManagerAddress request) throws GNotAllowed, GNotFound {

        return service.edit(request);
    }

    @GetMapping("/managerAddresses")
    @ApiOperation("Получить отфисписок всех адресов")
    public Page<RsManagerAddressBook> getFilteredPage(@RequestBody ManagerAddressBookFilter request) {

        return service.getFilteredPage(request);
    }
}
