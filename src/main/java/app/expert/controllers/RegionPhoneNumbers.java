package app.expert.controllers;

import app.base.constants.GErrors;
import app.base.controllers.GControllerAdvice;
import app.base.exceptions.GAlreadyExists;
import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.models.GResponse;
import app.expert.models.region.*;
import app.expert.services.FileService;
import app.expert.services.RegionPhoneNumberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/static")
@Api(tags = "2029. Управление ABC/DEF номерами")
@RequiredArgsConstructor
public class RegionPhoneNumbers extends GControllerAdvice {

    private final RegionPhoneNumberService regPhoneNumberService;
    private final FileService fileService;

    /**
     * Общие методы для работы с DEF/ABC номерами с регионами
     */

    @DeleteMapping("/regionPhoneNumbers")
    @ApiOperation("Удалить ABC/DEF номер с кодом региона")
    public GResponse delete(@RequestParam Long id) throws GNotFound {
        regPhoneNumberService.delete(id);
        return new GResponse(GErrors.OK);
    }


    @GetMapping("/regionPhoneNumbers")
    @ApiOperation("Получить пагинированный список ABC/DEF номеров с регионами")
    public Page<RsRegionPhoneNumber> getPaging(@RequestParam int page, @RequestParam int size) {
        return regPhoneNumberService.getPaging(page, size);
    }

    /**
     * Методы для ABC номеров с регионами
     */

    @GetMapping("/regionPhoneNumbers/ABC")
    @ApiOperation("Получить пагинированный список ABC номеров с регионами")
    public Page<RsABCRegionPhoneNumber> getABCPaging(@RequestParam int page, @RequestParam int size) {
        return regPhoneNumberService.getABCPaging(page, size);
    }

    @PutMapping("/regionPhoneNumber/ABC")
    @ApiOperation("Сохранить ABC номер с кодом региона")
    public RsABCRegionPhoneNumber saveABC(@ModelAttribute("abc") RqABCRegionPhoneNumber rq) throws GAlreadyExists {
        return regPhoneNumberService.saveABC(rq);
    }



    @PatchMapping("/regionPhoneNumber/ABC")
    @ApiOperation("Изменить ABC номер с кодом региона")
    public RsABCRegionPhoneNumber updateABC(@RequestParam Long id,
                                            @ModelAttribute RqABCRegionPhoneNumber rq) throws GNotFound, GNotAllowed {
        return regPhoneNumberService.updateABC(id, rq);
    }

    /**
     * Методы для DEF номеров с регионами
     */

    @GetMapping("/regionPhoneNumbers/DEF")
    @ApiOperation("Получить пагинированный список DEF номеров с регионами")
    public Page<RsDEFRegionPhoneNumber> getDEFPaging(@RequestParam int page, @RequestParam int size) {
        return regPhoneNumberService.getDEFPaging(page, size);
    }

    @PutMapping("/regionPhoneNumber/DEF")
    @ApiOperation("Сохранить DEF номер с кодом региона")
    public RsDEFRegionPhoneNumber saveDEF(@ModelAttribute("abc") RqDEFRegionPhoneNumber rq) throws GAlreadyExists {
        return regPhoneNumberService.saveDEF(rq);
    }

    @PatchMapping("/regionPhoneNumber/DEF")
    @ApiOperation("Изменить ABC номер с кодом региона")
    public RsDEFRegionPhoneNumber updateDEF(
            @RequestParam Long id,
            @ModelAttribute RqDEFRegionPhoneNumber rq) throws GNotFound, GNotAllowed {
        return regPhoneNumberService.updateDEF(id, rq);
    }

    /**
     * Загрузка файла с ABC/DEF номерами
     */
    @PostMapping(value = "/regionPhoneNumbers/upload_file" , consumes = {"text/csv"})
    @ApiOperation("Загурзить файл с ABC/DEF телефонами")
    public GResponse uploadFile(@RequestBody String content) throws IOException, GException {
        fileService.processFile(content);
        return new GResponse(GErrors.OK) ;
    }

    @GetMapping(value = "/regionPhoneNumber/uploaded_files" )
    @ApiOperation("Получить информацию по загруженным файлам")
    public List<FileInfo> getFilesInfo(){
        return  regPhoneNumberService.getFileInfo();
    }
}
