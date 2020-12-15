package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.utils.PasswordUtils;
import app.expert.constants.ConfigCode;
import app.expert.db.configs.Config;
import app.expert.db.configs.ConfigCache;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Api(tags = "3007. Конфиги")
public class Configs extends GControllerAdvice {

    private final ConfigCache cache;
    private final ManagerCache managerCache;

    @PutMapping("/config")
    @ApiOperation("Создать конфиг")
    public Config create(@RequestParam ConfigCode code, @RequestParam String value) {
        return cache.save(Config.builder()
                .key(code.getKey())
                .value(value)
                .build());
    }

    @GetMapping("/config")
    @ApiOperation("Запросить конфиг по коду")
    public Config read(@RequestParam ConfigCode code) throws GNotAllowed, GNotFound {
        return cache.find(code.getKey());
    }

    @PatchMapping("/config")
    @ApiOperation("Изменить конфиг по коду")
    public Config update(@RequestParam ConfigCode code, @RequestParam String value) throws GNotAllowed, GNotFound {
        Config config = cache.find(code.getKey());
        config.setValue(value);
        return cache.save(config);
    }

    @GetMapping("/configs")
    @ApiOperation("Получить все конфиги")
    public List<Config> getAll() {
        return cache.getAll();
    }

    @PostMapping("/superUserPass")
    @ApiOperation("Поменять пароль суперюзера")
    public void changePass(@RequestParam String oldPassword, @RequestParam String newPassword) throws GNotFound, GNotAllowed, GBadRequest {

        Manager manager = managerCache.findByUsername("superuser");
        String hash = PasswordUtils.generateUserPassword(oldPassword, app.expert.constants.Configs.SUPERUSER_SALT.getValue());
        if (!hash.equals(manager.getAgentPasswordHash())) {
            throw new GBadRequest();
        }

        hash = PasswordUtils.generateUserPassword(newPassword, app.expert.constants.Configs.SUPERUSER_SALT.getValue());
        manager.setAgentPasswordHash(hash);
        managerCache.save(manager);
    }
}
