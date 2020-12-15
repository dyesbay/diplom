package app.expert.services;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.db.statics.request_field_sub_subjects.RequestFieldSubSubject;
import app.expert.db.statics.request_field_sub_subjects.RequestFieldSubSubjectKey;
import app.expert.db.statics.request_field_sub_subjects.RqFieldSubSubjectCache;
import app.expert.db.statics.request_field_sub_subjects.RqFieldSubSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RequestFieldSubSubjectService {

    private final RqFieldSubSubjectCache cache;
    private final RqFieldSubSubjectRepository repository;

    public RequestFieldSubSubject findByFieldAndSubject(Long field, Long subSubject) throws GNotFound, GNotAllowed {
        RequestFieldSubSubjectKey rqFieldSubSubjectKey =
                RequestFieldSubSubjectKey
                .builder()
                .requestField(field)
                .subSubject(subSubject)
                .build();
        return cache.find(rqFieldSubSubjectKey);
    }
}
