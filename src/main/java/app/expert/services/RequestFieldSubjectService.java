package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GException;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.utils.ObjectUtils;
import app.expert.constants.InputType;
import app.expert.db.statics.request_field_sub_subjects.RequestFieldSubSubject;
import app.expert.db.statics.request_field_sub_subjects.RequestFieldSubSubjectKey;
import app.expert.db.statics.request_field_sub_subjects.RqFieldSubSubjectCache;
import app.expert.db.statics.request_field_subjects.RequestFieldSubject;
import app.expert.db.statics.request_field_subjects.RequestFieldSubjectCache;
import app.expert.db.statics.request_field_subjects.RequestFieldSubjectRepository;
import app.expert.db.statics.request_field_subjects.RqFieldSubjectKey;
import app.expert.db.statics.subjects.SubjectCache;
import app.expert.models.request.MField;
import app.expert.models.request.RsRequestField;
import app.expert.models.request.RsRequestFieldValue;
import app.expert.models.sub_subject.RsSubSubject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RequestFieldSubjectService {

    private final RequestFieldSubjectCache cache;
    private final RequestFieldSubjectRepository repository;
    private final RqFieldSubSubjectCache subSubCache;
    private final SubSubjectService subSubjectService;
    private final RequestFieldService requestFieldService;
    private final RequestFieldValueService requestFieldValueService;
    private final SubjectCache subjectCache;

    public RequestFieldSubject findByFieldAndSubject(Long field, Long subject) throws GNotFound, GNotAllowed {
        RqFieldSubjectKey rqFieldSubjectKey = RqFieldSubjectKey.builder()
                .requestField(field)
                .subject(subject)
                .build();
        return cache.find(rqFieldSubjectKey);
    }

    public List<RequestFieldSubject> findAllBySubject(Long subject) {
        return  repository.findBySubject(subject);
    }

    public RequestFieldSubSubject findByFieldAndSubSubject(Long field, Long subSubject) throws GNotFound, GNotAllowed {
        RequestFieldSubSubjectKey rqFieldSubSubjectKey =
                RequestFieldSubSubjectKey
                        .builder()
                        .requestField(field)
                        .subSubject(subSubject)
                        .build();
        return subSubCache.find(rqFieldSubSubjectKey);
    }


    /**
     * @param subSubject - id подтематики
     * @param subject    - id тематики
     * @throws GNotFound   - если subject не найден
     * @throws GNotAllowed - если subject disabled
     * @throws GBadRequest - если subSubject не относится к subject
     */
    private void checkSubSubject(Long subSubject, Long subject) throws GException {
        List<Long> subSubjects = subSubjectService.getBySubject(subject).stream()
                .map(RsSubSubject::getId).collect(Collectors.toList());
        if (!subSubjects.contains(subSubject)) throw new GBadRequest();
    }

    /**
     * функция для проверки наличия обязательных полей
     *
     * @param allFields             - список всех возможных полей для запроса по тематике
     * @param incomingRequestFields - список полей во входящем запросе
     * @throws GBadRequest - в случае если в списке
     *                     из входящего запроса отсутствуют обязательные поля
     */
    private void checkRequiredFields(List<RsRequestField> allFields, List<Long> incomingRequestFields) throws GBadRequest {

        // вытащим все айди обязательных полей
        List<Long> allRequiredFieldsIds = allFields.stream().filter(RsRequestField::getRequired)
                .map(RsRequestField::getId).collect(Collectors.toList());

        // если во входящем запросе содержатся
        // не все обязательные поля то выбрасываем ошибку
        if (!incomingRequestFields.containsAll(allRequiredFieldsIds)) throw new GBadRequest();
    }

    private void checkFields(MField field, RsRequestField requestField) throws GBadRequest {

        // проверить что записи типа TEXT не пустые
        if (requestField.getType().equals(InputType.TEXT)) {
            if (ObjectUtils.isBlank(field.getValue())) throw new GBadRequest();
        }

        // проверяем форммат DATE
        if (requestField.getType().equals(InputType.DATE)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            try {
                field.setValue(simpleDateFormat.format(simpleDateFormat.parse(field.getValue())));
            } catch (ParseException e) {
                throw new GBadRequest();
            }
        }

        // проверяем DATE_TIME
        if (requestField.getType().equals(InputType.DATE_TIME)) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            try {
                field.setValue(simpleDateFormat.format(simpleDateFormat.parse(field.getValue())));
            } catch (ParseException e) {
                throw new GBadRequest();
            }
        }
    }

    /**
     * Метод валидирует поля из запроса
     *
     * @param data    - массив полей MField
     * @param subject - id тематики
     * @throws GNotFound   - если поля для данного subject не существует
     * @throws GNotAllowed - если поле есть но отключено
     * @throws GBadRequest - если поле пустое
     */
    private void checkSubjectsFields(List<MField> data, Long subject) throws GNotFound, GNotAllowed, GBadRequest {
        for (MField field : data) {

            // проверяем что поле относится к данному subject,
            // если нет то метод выбросит исключение
            findByFieldAndSubject(field.getRequestField(), subject);

            RsRequestField requestField = requestFieldService.get(field.getRequestField());
            field.setRequestFieldName(requestField.getName());
            // проверить что во входящем запрсое записи
            // типа TEXT_SEARCH не пустые и связаны с тематикой запроса
            if (requestField.getType().equals(InputType.TEXT_SEARCH)
                    || requestField.getType().equals(InputType.SELECT)) {

                // вытаскиваем значение из запроса, в случае ошибки метод выбросит исключение
                if(field.getRequestFieldValue() != null){
                    RsRequestFieldValue fieldValue = requestFieldValueService.get(field.getRequestFieldValue());
                    field.setValue(fieldValue.getValue());
                    // нужно проверить что это значение связано с тематикой запроса
                    // - у нас есть id поля поэтому сделаем
                    // поиск по таблице request_field_subject по id поля и тематике
                    // - если записи нет то будет исключение
                    findByFieldAndSubject(fieldValue.getField(), subject);
                }
            }

            // проверить поля
            checkFields(field, requestField);
        }
    }

    private void checkSubSubjectsFields(List<MField> data, Long subSubject) throws GNotFound, GNotAllowed, GBadRequest {
        for (MField field : data) {

            // проверяем что поле относится к данному subsSubject,
            // если нет то метод выбросит исключение
            findByFieldAndSubSubject(field.getRequestField(), subSubject);

            RsRequestField requestField = requestFieldService.get(field.getRequestField());
            field.setRequestFieldName(requestField.getName());

            // проверить что во входящем запрсое записи
            // типа TEXT_SEARCH не пустые и связаны с тематикой запроса
            if (requestField.getType().equals(InputType.TEXT_SEARCH)
                    || requestField.getType().equals(InputType.SELECT)) {

                // вытаскиваем значение из запроса, в случае ошибки метод выбросит исключение
                RsRequestFieldValue fieldValue = requestFieldValueService.get(field.getRequestFieldValue());
                field.setValue(fieldValue.getValue());

                // нужно проверить что это значение связано с тематикой запроса
                // - у нас есть id поля поэтому сделаем
                // поиск по таблице request_field_subject по id поля и тематике
                // - если записи нет то будет исключение
                findByFieldAndSubSubject(fieldValue.getField(), subSubject);
            }

            // проверить поля
            checkFields(field, requestField);

        }
    }

    /**
     * метод обертка для валидации тематик и полей из запроса
     *
     * @param subject    - айди тематики
     * @param subSubject - айди подтематики
     * @param fields     - данные по тематике
     */

    public void validateSubjectsAndField(Long subject, Long subSubject, List<MField> fields) throws GException {

        // проверяем что subject с таким id существует
        subjectCache.find(subject);

        // если subSubject > 0 то валидируем поля подтематики, если нет то тематики
        if (subSubject != null && subSubject > 0) {
            checkSubSubject(subSubject, subject);

            List<RsRequestField> allPossibleSubSubjectFields = requestFieldService.getBySubSubject(subSubject);
            List<Long> incomingRequestFields = fields.stream()
                    .map(MField::getRequestField).collect(Collectors.toList());
            checkRequiredFields(allPossibleSubSubjectFields, incomingRequestFields);
            checkSubSubjectsFields(fields, subSubject);
        } else {

            // получаем все возможные поля для запроса по этой тематике
            List<RsRequestField> allPossibleSubjectRequestFields = requestFieldService.getBySubject(subject);

            // получаем поля из запроса который нам пришел
            List<Long> incomingRequestFields = fields.stream()
                    .map(MField::getRequestField).collect(Collectors.toList());

            // проверить что все обязательные поля присутствуют
            checkRequiredFields(allPossibleSubjectRequestFields, incomingRequestFields);

            // проверяем поля во входящем запросе, в случае ошибки метод выбросит исключение
            checkSubjectsFields(fields, subject);

            // если subSubject > 0 то проверить что такой существует и относится к данному subject
            if (subSubject != null)
                checkSubSubject(subSubject, subject);
        }
    }
}
