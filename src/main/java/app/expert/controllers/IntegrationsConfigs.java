package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.db.statics.integrations_configs.IntegrationConfig;
import app.expert.db.statics.integrations_configs.IntegrationConfigCache;
import app.expert.models.RqIntegrationConfig;
import app.expert.services.IntegrationConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/static")
@Api(tags = "2031. Конифгурации для интеграций с внешними сервисами")
public class IntegrationsConfigs extends GControllerAdvice {

    private final IntegrationConfigCache configCache;
    private final IntegrationConfigService service;

    @GetMapping("/integrationConfig")
    @ApiOperation("Получить конфиг по айди")
    public IntegrationConfig get(@RequestParam String code) throws GNotAllowed, GNotFound {
        return configCache.find(code);
    }

    @PutMapping("/integrationConfig")
    @ApiOperation("Создать конфиг")
    public IntegrationConfig add(@Validated @RequestBody RqIntegrationConfig request) throws GNotAllowed, GNotFound {
        return service.add(request);
    }
}
