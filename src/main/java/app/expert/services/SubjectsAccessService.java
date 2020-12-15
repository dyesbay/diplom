package app.expert.services;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.models.GResponse;
import app.expert.constants.AccessType;
import app.expert.constants.Platform;
import app.expert.db.configs.Config;
import app.expert.db.configs.ConfigsCache;
import app.expert.db.statics.sub_subject.SubSubject;
import app.expert.db.statics.sub_subject.SubSubjectCache;
import app.expert.db.statics.subjects.Subject;
import app.expert.db.statics.subjects.SubjectCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static app.base.constants.GErrors.OK;

@Service
@RequiredArgsConstructor
public class SubjectsAccessService {

    private final SubjectCache subjectCache;
    private final SubSubjectCache subSubjectCache;
    private final ConfigsCache configsCache;

    public String buildConfigKey(Long id, Platform platform) {
        return "subjects." + id + ".access_type." + platform.getKey();
    }

    private void processNewConfig(String key, String value) {

        // если конфига нет то сохраняем, в противном случае ничего не делаем
        try {
            configsCache.find(key);
        } catch (GNotAllowed | GNotFound e) {
            configsCache.save(Config.builder().key(key).value(value).build());
        }
    }

    public void addAccessConfig(Long id, Platform platform, AccessType accessType) {

        processNewConfig(buildConfigKey(id, platform), accessType.getKey());
    }

    public void addDefaultAccessConfig(Long id) {

        processNewConfig(buildConfigKey(id, Platform.KAZAN), AccessType.ALL.getKey());
        processNewConfig(buildConfigKey(id, Platform.KURSK), AccessType.ALL.getKey());
    }

    /**
     * добавим дефолтные уровни доступа для всех тематик и подтематик по платформам
     */
    public void addDefaultSubjectConfigsForAll() {

        List<Long> subjects = subjectCache.getAll().stream().map(Subject::getId).collect(Collectors.toList());
        for (Long subject : subjects) {
            addDefaultAccessConfig(subject);
        }

        List<Long> subSubjects = subSubjectCache.getAll().stream().map(SubSubject::getId).collect(Collectors.toList());
        for (Long subSubject : subSubjects) {
            addDefaultAccessConfig(subSubject);
        }
    }

    public GResponse editSubjectAccess(Long subject, Platform platform, AccessType accessType) throws GNotAllowed, GNotFound {

        // ищем конфиг и меняем тип
        String key = buildConfigKey(subject, platform);
        Config config = configsCache.find(key);
        config.setValue(accessType.getKey());
        configsCache.save(config);
        return new GResponse(OK);
    }
}
