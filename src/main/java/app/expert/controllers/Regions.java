package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.models.GResponse;
import app.expert.db.statics.region.Region;
import app.expert.models.region.RegionFilter;
import app.expert.services.RegionsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/static")
@Api(tags = "1001. Регионы")
@RequiredArgsConstructor
public class Regions extends GControllerAdvice {

    private final RegionsService service;

    @GetMapping("/region")
    @ApiOperation("Получить регион по id")
    public Region get(@RequestParam Long id) throws GException {
        return service.find(id);
    }

    @PutMapping("/region")
    @ApiOperation("Добавление нового региона")
    public Region add(@RequestParam String name) throws GException {
        return service.add(name);
    }

    @PatchMapping("/region")
    @ApiOperation("Изменение данных региона")
    public Region edit(@RequestParam Long id,
                       @RequestParam String name) throws GException {
        return service.edit(id, name);
    }

    @DeleteMapping("/region")
    @ApiOperation("Удалить регион")
    public GResponse delete(@RequestParam Long id) throws GException {
        service.delete(id);
        return new GResponse(GErrors.OK);
    }

    @GetMapping("/regions")
    @ApiOperation("Получить все регионы с пагинацией")
    public Page<Region> getAll(@ModelAttribute("regionFilter")RegionFilter filter) {
        return service.getFilteredPagingList(filter);
    }
    
    @GetMapping("/region/phone")
    @ApiOperation("Получить регион по номеру телефона")
    public Region getByPhoneCode(@RequestParam String number) throws GException {
        return service.getRegionByPhone(number);
    }
}
