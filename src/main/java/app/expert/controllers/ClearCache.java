package app.expert.controllers;

import app.base.RedisService;
import app.base.models.GResponse;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static app.base.constants.GErrors.OK;

@RequiredArgsConstructor
@RestController
@Api(tags = "Почистить кеш")
public class ClearCache {

    private final RedisService redisService;

    @Value("${info.app.name}")
    protected String system;

    @PostMapping("/clearCache")
    public GResponse clearCache() {
        Set<String> keys = redisService.keys(system + ".*");
        keys.addAll(redisService.keys(system + "*" + "all"));
        redisService.del(keys.toArray(new String[0]));
        return new GResponse(OK);
    }
}
