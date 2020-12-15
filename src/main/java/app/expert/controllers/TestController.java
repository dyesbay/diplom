package app.expert.controllers;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.services.ExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final ExternalService externalService;

    @GetMapping("/test/login")
    public String getTest() throws GNotAllowed, GBadRequest, GNotFound, IOException {
        return externalService.loginEsroo();
    }

    @GetMapping("/test/get")
    public String get() throws GNotAllowed, GBadRequest, GNotFound, IOException {
        return externalService.get();
    }
}
