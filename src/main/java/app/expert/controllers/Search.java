package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GSystemError;
import app.expert.models.RqRequestAdmission;
import app.expert.models.RsRequestOrAdmission;
import app.expert.services.SearchService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@ControllerAdvice
@Api(tags = "1022. Поиск")
@RequiredArgsConstructor
@RequestMapping("/search")
public class Search extends GControllerAdvice {

    private final SearchService service;

    @GetMapping("/requests")
    public Page<RsRequestOrAdmission> searchRequestOrAdmissions(
            @ModelAttribute RqRequestAdmission rq) throws GSystemError {
        return service.findRequestsAndAdmissions(rq);
    }

    @GetMapping("/names")
    public List<String> findNames(@RequestParam String pattern, @RequestParam(required = false,defaultValue = "10") Integer size){
        return service.findNames(pattern,size);
    }

    @GetMapping("/surnames")
    public List<String> findSurnames(@RequestParam String pattern, @RequestParam(required = false,defaultValue = "10") Integer size){
        return service.findSurnames(pattern,size);
    }
}
