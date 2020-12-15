package app.expert.services;

import app.base.utils.SerializationUtils;
import app.expert.db.statics.names.NamesRepository;
import app.expert.db.statics.surnames.SurnamesRepository;
import app.expert.models.MName;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NamesLoaderService {

    private final NamesRepository namesRepository;
    private final SurnamesRepository surnamesRepository;
    private final Logger logger = LoggerFactory.getLogger(NamesLoaderService.class);

    @Transactional
    public void loadNames() {
        if (namesRepository.count() == 0) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("names/russian_names.json"), StandardCharsets.UTF_8))) {
                StringBuilder builder = new StringBuilder();
                reader.lines().forEachOrdered(builder::append);
                namesRepository.save(Arrays.stream(SerializationUtils.fromJson(builder.toString(), MName[].class))
                        .map(MName::convertToName)
                        .collect(Collectors.toList()));
                logger.info("Loaded names database");
            } catch (Exception ex) {
                logger.error("Failed to load names");
            }
        }
    }

    @Transactional
    public void loadSurnames() {
        if (surnamesRepository.count() == 0) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("names/russian_surnames.json"), StandardCharsets.UTF_8))) {
                StringBuilder builder = new StringBuilder();
                reader.lines().forEachOrdered(builder::append);
                surnamesRepository.save(Arrays.stream(SerializationUtils.fromJson(builder.toString(), MName[].class))
                        .map(MName::convertToSurname)
                        .collect(Collectors.toList()));
                logger.info("Loaded surnames database");

            } catch (Exception ex) {
                logger.error("Failed to load surnames");
            }
        }
    }

}
