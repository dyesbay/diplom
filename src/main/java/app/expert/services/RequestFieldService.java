package app.expert.services;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.constants.ExpertErrors;
import app.expert.constants.InputType;
import app.expert.db.statics.request_field_sub_subjects.RequestFieldSubSubject;
import app.expert.db.statics.request_field_sub_subjects.RequestFieldSubSubjectKey;
import app.expert.db.statics.request_field_sub_subjects.RqFieldSubSubjectCache;
import app.expert.db.statics.request_field_sub_subjects.RqFieldSubSubjectRepository;
import app.expert.db.statics.request_field_subjects.RequestFieldSubject;
import app.expert.db.statics.request_field_subjects.RequestFieldSubjectCache;
import app.expert.db.statics.request_field_subjects.RequestFieldSubjectRepository;
import app.expert.db.statics.request_field_subjects.RqFieldSubjectKey;
import app.expert.db.statics.request_field_values.RequestFieldValue;
import app.expert.db.statics.request_field_values.RequestFieldValueCache;
import app.expert.db.statics.request_field_values.RequestFieldValueRepository;
import app.expert.db.statics.request_fields.RequestField;
import app.expert.db.statics.request_fields.RequestFieldCache;
import app.expert.db.statics.sub_subject.SubSubject;
import app.expert.db.statics.sub_subject.SubSubjectCache;
import app.expert.db.statics.subjects.Subject;
import app.expert.db.statics.subjects.SubjectCache;
import app.expert.models.request.RqRequestField;
import app.expert.models.request.RsRequestField;
import app.expert.models.request.RsRequestFieldValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RequestFieldService {

    private final RequestFieldCache cache;
    private final RequestFieldValueCache valCache;
    private final RequestFieldValueRepository valRepository;
    private final RequestFieldSubjectRepository rqFieldSubjectRepo;
    private final RequestFieldSubjectCache rqFieldSubjectCache;
    private final SubjectCache subjectCache;
    private final RqFieldSubSubjectRepository rqFieldSubjectCloneRepo;
    private final RqFieldSubSubjectCache rqFieldSubSubjectCache;
    private final SubSubjectCache subSubjectCache;

    /**
     * Возвращает все поля относящиеся к тематике
     * @param id - иденитификатор темы обращения
     */
    public List<RsRequestField> getBySubject(Long id) throws GNotFound, GNotAllowed {
        List<RequestFieldSubject> rqFieldSubjects = rqFieldSubjectRepo.findBySubject(id);
        List<RsRequestField> result = new LinkedList<>();
        for(RequestFieldSubject rqFieldSub : rqFieldSubjects) {
            RsRequestField rs = get(rqFieldSub.getRequestField());
            rs.setRequired(rqFieldSub.isRequired());
            result.add(rs);
        }
        return result;
    }

    /**
     * Возвращает все поля относящиеся к подтематике
     * @param id - иденитификатор подтематики обращения
     */
    public List<RsRequestField> getBySubSubject(Long id) throws GNotAllowed, GNotFound {
        List<RequestFieldSubSubject> rqFieldSubjects = rqFieldSubjectCloneRepo.findBySubSubject(id);
        List<RsRequestField> result = new LinkedList<>();
        for(RequestFieldSubSubject rqFieldSubClone : rqFieldSubjects) {
            RsRequestField rs = get(rqFieldSubClone.getRequestField());
            rs.setRequired(rqFieldSubClone.isRequired());
            result.add(rs);
        }
        return result;
    }

    /**
     * Метод проверяет к тематке или подтематике относиться поле,
     * сохраняет в базу, возвращает модель.
     * @param id - идентификатор поля в таблице request_fields
     */
    public RsRequestField get(Long id) throws GNotFound, GNotAllowed {
        if (rqFieldSubSubjectCache.findByRequestField(id) != null) {
            RequestFieldSubSubject rqFieldSubClone = rqFieldSubSubjectCache.findByRequestField(id);
            return RsRequestField.get(cache.find(id), rqFieldSubClone, valCache);
        } else if (rqFieldSubjectCache.findByRequestField(id) != null) {
            RequestFieldSubject rqFieldSub = rqFieldSubjectCache.findByRequestField(id);
            return RsRequestField.get(cache.find(id), rqFieldSub, valCache);
        } else {
            /*если поле запроса не найдена ни для тематики ни для подтематики*/
            throw new GNotFound(cache.getNotFoundError());
        }
    }

    /**
     * Сохраняет поле согласо полученным параметрам.
     * Сохраняет зависимости в базу: значения поля, если поле таковые имеет;
     * а так же сохраняет зависимоти поле/тематика или поле/подтематика.
     * @param rq - модель данных
     * @return RsRequestField - модель данных для ответа
     * @throws GNotFound - не найдены тематика или подтематика
     * @throws GNotAllowed - если тематика и подтематика не заданны
     */
    public RsRequestField add(RqRequestField rq) throws GNotFound, GNotAllowed {
        validRq(rq);
        if(rq.getSubject() != null && rq.getSubSubject() == null) {
            return saveAsSubjectField(rq);
        } else if (rq.getSubSubject() != null && rq.getSubject() == null) {
            return saveAsSubSubjectField(rq);
        } else if(rq.getSubSubject() == null && rq.getSubject() == null) {
            throw new GNotAllowed(ExpertErrors.RQ_FIELD_REQUEST_PARAMETER_NOT_FOUND);
        } else {
            /*Если заданы 2 параметра: тематика и подтематика*/
            throw new GNotAllowed(ExpertErrors.RQ_FIELD_AMBIGUOUS_REQUEST_PARAMETER);
        }
    }

    /**
     * Методы удаляет поле по id и все зависимые Entities в БД
     */
    public void delete(Long id) throws GNotFound, GNotAllowed {
       for (RequestFieldSubject rqFieldSubject : rqFieldSubjectRepo.findAll()) {
            if(rqFieldSubject.getRequestField().equals(id))
                rqFieldSubjectRepo.delete(
                        RqFieldSubjectKey
                                .builder()
                                .requestField(rqFieldSubject.getRequestField())
                                .subject(rqFieldSubject.getSubject())
                                .build());
        }

       for (RequestFieldSubSubject rqFieldSubSubject : rqFieldSubjectCloneRepo.findAll()) {
            if(rqFieldSubSubject.getRequestField().equals(id))
                rqFieldSubjectCloneRepo.delete(
                        RequestFieldSubSubjectKey
                                .builder()
                                .requestField(rqFieldSubSubject.getRequestField())
                                .subSubject(rqFieldSubSubject.getSubSubject())
                                .build());
        }

       for(RequestFieldValue value : valRepository.findAll()) {
            if(value.getField().equals(id))
                valRepository.delete(value.getId());
        }
        cache.delete(id);
    }

    /**
     * Сохраняет поле для темы обращения
     */
    private RsRequestField saveAsSubjectField(RqRequestField rq) throws GNotAllowed, GNotFound {
        Subject subject = subjectCache.find(rq.getSubject());
        RequestField rqField = cache.save(rq.get());
        RequestFieldSubject rqFieldSub = rqFieldSubjectCache.save(RequestFieldSubject
                .builder()
                .required(rq.isRequired())
                .requestField(rqField.getId())
                .subject(subject.getId())
                .build());
        saveFieldValues(rq.getValues(), rqField);
        return RsRequestField.get(rqField, rqFieldSub, valCache);
    }

    /**
     * Сорхраняет поле для подтематики обращения
     */
    private RsRequestField saveAsSubSubjectField(RqRequestField rq) throws GNotAllowed, GNotFound {
        SubSubject subSubject = subSubjectCache.find(rq.getSubSubject());
        RequestField rqField = cache.save(rq.get());
        RequestFieldSubSubject rqFieldSubClone = rqFieldSubSubjectCache.save(RequestFieldSubSubject
                .builder()
                .required(rq.isRequired())
                .requestField(rqField.getId())
                .subSubject(subSubject.getId())
                .build());
        saveFieldValues(rq.getValues(), rqField);
        return RsRequestField.get(rqField, rqFieldSubClone, valCache);
    }

    public void deleteValue(Long id) throws GNotFound {
       valCache.delete(id);
    }

    /**
     * Сохраняет значения полей если таковые были перереданны
     */
    private void saveFieldValues(String[] values, RequestField field) throws GNotFound {
        for(RsRequestFieldValue value : valCache.findByRequestField(field.getId())){
            deleteValue(value.getId());
        }
        if(values != null && values.length > 0 ) {
            Arrays.stream(values).forEach((value) -> valCache.save(
                    RequestFieldValue
                            .builder()
                            .field(field.getId())
                            .value(value)
                            .build()));
        }
    }

    /**
     * Валидация запроса
     */
    private void validRq(RqRequestField rq) throws GNotAllowed {
        if(
                (!rq.getInputType().equals(InputType.SELECT)
                && !rq.getInputType().equals(InputType.TEXT_SEARCH))
                && rq.getValues() != null) {
            throw new GNotAllowed(ExpertErrors.FIELD_DOES_NOT_SUPPORT_VALUES);
        }

        if(
                (rq.getInputType().equals(InputType.SELECT)
                        || rq.getInputType().equals(InputType.TEXT_SEARCH))
                        && rq.getValues() == null) {
            throw new GNotAllowed(ExpertErrors.FIELD_REQUIRES_VALUES);
        }
    }

}
