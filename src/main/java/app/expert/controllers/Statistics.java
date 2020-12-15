package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.utils.DateUtils;
import app.expert.models.statistics.*;
import app.expert.models.statistics.RqStat;
import app.expert.models.statistics.RsManagerStat;
import app.expert.models.statistics.RsStat;
import app.expert.models.statistics.StatRequestFilter;
import app.expert.services.ManagersStatService;
import app.expert.services.StatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Api(tags = "Статистика руководителя ООЗ")
public class Statistics extends GControllerAdvice {

    private final StatService service;
    private final ManagersStatService managerStatService;

    @GetMapping("/statistics")
    @ApiOperation("Получить общую статистику для руководителя ООЗ")
    public RsStat getStat(@ModelAttribute("stat") RqStat rq) throws GBadRequest {
        return service.getStat(rq);
    }

    @GetMapping("/statistics/requests")
    @ApiOperation("Получить статистику по ОЗ")
    public Map<String, Object> get(@Validated @ModelAttribute("requestStat") StatRequestFilter rq) throws GBadRequest {
        return service.countRequests(rq);
    }

    @GetMapping("/statistics/requests/common")
    @ApiOperation("Получить общую статистику для ОЗ")
    public RsRequestStat get(@RequestParam(value = "from") @DateTimeFormat(pattern = DateUtils.HUMAN_DATE) Date from,
                             @RequestParam(value = "to") @DateTimeFormat(pattern = DateUtils.HUMAN_DATE) Date to) {
        return service.getCommonRequestStat(from, to);
    }

    @GetMapping("/managers/statistics")
    @ApiOperation("Получить статистику по сотрудникам для руководителя ООЗ")
    public RsManagerStat getManagerStat(@ModelAttribute("stat") RqStat rq) throws GBadRequest, GNotAllowed, GNotFound {
        return managerStatService.getStat(rq);
    }

    @GetMapping("/managers/distribution_stat")
    @ApiOperation("Статистика для распределения ОЗ")
    public RsManagerStat getManagersDistributionStat(@ModelAttribute("stat") RqStat rq) throws GBadRequest, GNotAllowed, GNotFound {
        return managerStatService.getStatForDistribution(rq);
    }
}
