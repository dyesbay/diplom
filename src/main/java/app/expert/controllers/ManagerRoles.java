package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.db.statics.manager_role.ManagerRole;
import app.expert.services.ManagerRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/static")
@Api(tags = "1005. Роли")
public class ManagerRoles extends GControllerAdvice {
        
    private final ManagerRoleService service;
    
    @GetMapping("/managerRole")
    @ApiOperation("Поулчить роль")
    public ManagerRole get(@RequestParam String code) throws GException {
        return service.get(code);
    }
    
    @PutMapping("/mangerRole")
    @ApiOperation("Добавить роль")
    public ManagerRole add(@RequestParam String code,
                            @RequestParam String name, 
                            @RequestParam(required = false) String description) throws GException {
        return service.add(code, name, description);
    }
    
    @DeleteMapping("/managerRole")
    @ApiOperation("Удалить роль")
    public GResponse delete(@RequestParam String code) throws GException {
        service.delete(code);
        return new GResponse(GErrors.OK);
    }
    
    @PatchMapping("/managerRole")
    @ApiOperation("Обновить данные роли")
    public ManagerRole update(@RequestParam String code,
                               @RequestParam String name, 
                               @RequestParam(required = false) String description) throws GException {
        return service.update(code, name, description);
    }
}
