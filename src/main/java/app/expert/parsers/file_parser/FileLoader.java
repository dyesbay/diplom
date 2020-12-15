package app.expert.parsers.file_parser;

import app.expert.db.statics.region_phone_numbers.RegionPhoneNumberRepository;
import app.expert.parsers.file_parser.entity.RawRegionPhoneCode;
import app.expert.services.FileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FileLoader {

    private static final Logger log = LoggerFactory.getLogger(FileLoader.class.getSimpleName());

    private final FileService fileService;
    private final RegionPhoneNumberRepository regionPhoneNumberRepo;

    public void loadFileFromResource() {
        if (regionPhoneNumberRepo.count() == 0){
            try {
                log.info("Loading region phone numbers files");
                for (String content : readRegionPhoneNumberFiles()) {
                    List<RawRegionPhoneCode> rawContent = FileRegionPhoneNumberParser.parse(content);
                    fileService.saveIntoBase(rawContent);
                }
            } catch (Exception ex) {
                log.error("Failed to load region phone numbers files.");
            }
        }
    }

    private List<String> readRegionPhoneNumberFiles() {
        List<String> filesContent = new LinkedList<>();
        for(String path : FilePathHolder.getAllResourcePaths()) {
            StringBuilder builder = new StringBuilder();
            try(BufferedReader buffer =
                        new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8))) {
                while (buffer.ready()) {
                    builder.append(buffer.readLine()).append('\n');
                }
            } catch(IOException e) {
                log.error("Возникли проблемы с чтением файла" + path, e);
            }
            filesContent.add(builder.toString());
        }
        log.info("Чтение ABC/DEF файлов завершена успешно");
        return filesContent;
    }
}
