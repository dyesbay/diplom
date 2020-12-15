package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.expert.db.statics.message_tmpl.MessageTmplCache;
import app.expert.db.statics.message_tmpl.MessegeTmpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "1029. Шаблоны сообщений")
@RequestMapping("/static")
@RequiredArgsConstructor
public class MessageTemplates extends GControllerAdvice {

    private final MessageTmplCache cache;
    
    @GetMapping("/messageTemplate")
    @ApiOperation("Получить шаблон письма по id")
    public MessegeTmpl get(@RequestParam Long id) throws GException {
        return cache.find(id);
    }
}
