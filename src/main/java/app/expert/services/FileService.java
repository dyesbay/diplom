package app.expert.services;

import app.base.exceptions.GException;
import app.expert.constants.ExpertErrors;
import app.expert.db.statics.region.RegionsCache;
import app.expert.db.statics.region.RegionsRepository;
import app.expert.db.statics.region_phone_numbers.RegionPhoneNumber;
import app.expert.db.statics.region.Region;
import app.expert.db.statics.region_phone_numbers.RegionPhoneNumberRepository;
import app.expert.db.uploaded_files.UploadedFileRepository;
import app.expert.parsers.file_parser.FileRegionPhoneNumberParser;
import app.expert.parsers.file_parser.FileSaver;
import app.expert.parsers.file_parser.entity.RawRegionPhoneCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {
    
    private final static List<RegionPhoneNumber> toBase = new LinkedList<>();

    private final RegionPhoneNumberRepository regionPhoneNumberRepository;
    private final RegionsCache regCache;
    private final RegionsRepository regRepository;
    private final FileSaver fileSaver;
    private final UploadedFileRepository upFileRepo;

    public void processFile(String content) throws IOException, GException {
        checkSize(content);
        FileSaver saver = new FileSaver();
        saver.setUpFileRepo(upFileRepo);
        saver.setContent(content);
        new Thread(saver).start(); // сохраняем полученный контент в файл в отдельном потоке
        List<RawRegionPhoneCode> fromFile = FileRegionPhoneNumberParser.parse(content);
        saveIntoBase(fromFile);
    }

    /**
     * Сохраняем в базу то, что было полученно на входе.
     * @param rawRegionPhoneCodes - entity озданное при парсинге файла
     */
    public void saveIntoBase(List<RawRegionPhoneCode> rawRegionPhoneCodes) {
        List<Region> regions = regRepository.findAll();
        List<RegionPhoneNumber> codes = regionPhoneNumberRepository.findAll();
        for(RawRegionPhoneCode raw : rawRegionPhoneCodes) {
            if(raw.getCode().startsWith("3") || raw.getCode().startsWith("4"))
                saveAsABCPhone(raw,findRegion(raw,regions),codes);
            if(raw.getCode().startsWith("9") || raw.getCode().startsWith("8"))
                saveAsDefPhone(raw,findRegion(raw,regions),codes);
        }
        if(!toBase.isEmpty()){
            regionPhoneNumberRepository.save(toBase);
        }
        toBase.clear();
    }

    /**
     * Метод ищет регион по названию,
     * сравнивая названия регионов в базе с полученными из файла
     */
    private Region findRegion(RawRegionPhoneCode rawRegionPhoneCodes, List<Region> regions) {
        for (Region region : regions) {
            if(rawRegionPhoneCodes.getRegion().contains(region.getName())){
                return region;
            } if (region.getName().contains(rawRegionPhoneCodes.getRegion())){
                return region;
            }
        }
        return Region.builder()
                .id(null)
                .name("Не удалось определить")
                .build();
    }

    /**
     *  Метод сохраняет городские телефонны для регионов,
     *  исключая дублирование кодов и регионов
     */
    private void saveAsABCPhone(RawRegionPhoneCode raw, Region region, List<RegionPhoneNumber> codes) {
        if (region.getId() != null){
            RegionPhoneNumber newItem = RegionPhoneNumber.builder()
                    .code(Short.parseShort(raw.getCode()))
                    .dFrom(0)
                    .dTo(0)
                    .region(region.getId())
                    .build();
            if(codes.stream().noneMatch(s -> s.equalsTo(newItem)) &&
                    toBase.stream().noneMatch((s -> s.equalsTo(newItem))))
                toBase.add(newItem);
        }
    }

    /**
     * Метод сохраняет мобильные телефоны для регионов, включая диапазон номеров.
     * Диапазоны нужны, так как один
     * и тот же телефонный код может относиться к разным регионам.
     */
    private void saveAsDefPhone(RawRegionPhoneCode raw, Region region, List<RegionPhoneNumber> codes) {
        if (region.getId() != null){
            RegionPhoneNumber newItem = RegionPhoneNumber.builder()
                    .code(Short.parseShort(raw.getCode()))
                    .dFrom(raw.getFrom())
                    .dTo(raw.getTo())
                    .region(region.getId())
                    .build();
            if(codes.stream().noneMatch(s -> s.equalsTo(newItem)) &&
                    toBase.stream().noneMatch((s -> s.equalsTo(newItem))))
                toBase.add(newItem);
        }
    }

    private void checkSize(String content) throws GException {
        if ((double)content.getBytes().length/(1024*1024) > 20)
            throw new GException(ExpertErrors.EXCEEDED_REGION_PHONE_NUMBERS_SIZE_FILE);
    }
}
