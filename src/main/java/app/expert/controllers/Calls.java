package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GException;
import app.base.exceptions.GNotFound;
import app.base.utils.DateUtils;
import app.expert.db.call.Call;
import app.expert.models.call.RqCompCall;
import app.expert.models.call.RqRegCall;
import app.expert.services.CallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Api(tags = "1015. Звонки")
public class Calls extends GControllerAdvice {

    private final CallService service;
    private static final String DATE_FORMAT = DateUtils.HUMAN_DATE_TIME;
    private static final String TIME_FORMAT = "hh:mm:ss";

    @InitBinder({"compCall", "regCall", "onHold"})
    public void dateBinding(WebDataBinder binder) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        SimpleDateFormat timeFormatter = new SimpleDateFormat(TIME_FORMAT);
        formatter.setTimeZone(TimeZone.getDefault());
        timeFormatter.setTimeZone(TimeZone.getDefault());
        binder.registerCustomEditor(Date.class, "ended", new CustomDateEditor(formatter, true));
        binder.registerCustomEditor(Date.class, "started", new CustomDateEditor(formatter, true));
        binder.registerCustomEditor(Date.class, "onHold", new CustomDateEditor(timeFormatter, true));
    }

    @GetMapping("/call")
    @ApiOperation("Получить информацию о звонке")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 401, message = "Unauthorized")
    })
    public Call get(String identifier) throws GNotFound {
        return service.getByIdentifier(identifier);
    }

    @PutMapping("/call")
    @ApiOperation("Зарегестрировать звонок")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Already Exists"),
    })
    public Call register(@Validated @ModelAttribute("regCall") @NotNull RqRegCall rq) throws GException, ParseException {
        return service.registerCall(rq);
    }

    @PatchMapping("/call")
    @ApiOperation("Завершить звонок")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Already Exists"),
            @ApiResponse(code = 404, message = "Not Found"),
    })
    public Call complete(@Validated @ModelAttribute("compCall")@NotNull RqCompCall rq) throws GException, ParseException {
        return service.completeCall(rq);
    }
}
