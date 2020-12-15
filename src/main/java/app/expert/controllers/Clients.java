package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.models.client.ClientFilter;
import app.expert.models.client.RqClient;
import app.expert.models.client.RsClient;
import app.expert.services.ClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@ControllerAdvice
@Api(tags = "1019. Клиенты")
@RequestMapping
@RequiredArgsConstructor
public class Clients extends GControllerAdvice {
    
    private final ClientService service;

    @PutMapping("/client")
    @ApiOperation("Создать клиента или обновить информацию по номеру")
    public RsClient createOrUpdate(@Validated @RequestBody RqClient request) throws GException {
        return service.createOrUpdate(request);
    }
    
    @GetMapping("/client")
    @ApiOperation("Получить клиента по id или по номеру телефона")
    public RsClient get(@RequestParam(required = false) Long id, @RequestParam(required = false) String phone) throws GException {
        return service.getByPhoneOrId(id, phone);
    }
    
    @DeleteMapping("/client")
    @ApiOperation("Удалить клиента по id или по номеру телефона")
    public GResponse delete(@RequestParam(required = false) Long id,@RequestParam(required = false) String phone) throws GException {
        service.delete(id, phone);
        return new GResponse(GErrors.OK);
    }
    
    @GetMapping("/client/search")
    @ApiOperation("Получить отфильтрованный список клиентов с пагинацией")
    public Page<RsClient> get(@ModelAttribute("clientFilter") ClientFilter rq) throws GException {
        return service.getFilteredPage(rq);
    }
}
