package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.db.statics.department_address_book.DepartmentAddressBook;
import app.expert.db.statics.department_address_book.DepartmentAddressBookCache;
import app.expert.models.department_address_book.DepartmentFilter;
import app.expert.models.department_address_book.RqDepartmentAddressBook;
import app.expert.models.department_address_book.RqEditDepartmentAddress;
import app.expert.models.department_address_book.RsDepartmentAddressBook;
import app.expert.services.DepartmentAddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/static")
@Api(tags = "3003. Адресная книга органов")
public class DepartmentsAddressBook extends GControllerAdvice {

    private final DepartmentAddressBookService service;
    private final DepartmentAddressBookCache cache;

    @PutMapping("/departmentAddressBook")
    @ApiOperation("Добавить орган в адрсеную книгу")
    public RsDepartmentAddressBook add(@Validated @RequestBody RqDepartmentAddressBook request) {

        return RsDepartmentAddressBook.getFromEntity
                (cache.save(DepartmentAddressBook.getFromRequest(request)));
    }

    @PatchMapping("/departmentAddressBook")
    @ApiOperation("Редактировать орган")
    public RsDepartmentAddressBook edit(@Validated @RequestBody RqEditDepartmentAddress request) throws GNotAllowed, GNotFound {

        return service.edit(request);
    }

    @GetMapping("/departmentAddresses")
    @ApiOperation("Получить отфисписок всех адресов")
    public Page<RsDepartmentAddressBook> getFilteredPage(@RequestBody DepartmentFilter request) {

        return service.getFilteredPage(request);
    }
}
