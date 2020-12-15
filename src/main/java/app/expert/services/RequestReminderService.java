package app.expert.services;

import app.base.exceptions.GBadRequest;
import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.base.exceptions.GSystemError;
import app.base.models.GResponse;
import app.base.utils.DateUtils;
import app.expert.db.request.RequestCache;
import app.expert.db.request_reminders.RequestReminder;
import app.expert.db.request_reminders.RequestRemindersCache;
import app.expert.db.request_reminders.RequestRemindersRepository;
import app.expert.models.request.RqRequestReminder;
import app.expert.models.request.RqRequestReminderPostpone;
import app.expert.models.request.RsRequestReminder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static app.base.constants.GErrors.OK;

@RequiredArgsConstructor
@Service
public class RequestReminderService {
    
    private final RequestRemindersCache cache;
    private final RequestCache requestCache;
    private final RequestRemindersRepository repository;

    public List<RsRequestReminder> getByRequest (Long id) {
        return repository.findAllByRequestAndCheckedFalse(id)
                .stream().map(RsRequestReminder::getFromEntity).collect(Collectors.toList());

    }

    public List<RsRequestReminder> getByDate(Date date) {
        return repository.findAllBySignalAndCheckedFalse(date)
                .stream().map(RsRequestReminder::getFromEntity).collect(Collectors.toList());

    }
    
    public RsRequestReminder add(RqRequestReminder rq) throws GNotAllowed, GNotFound, GSystemError, GBadRequest {

        // проверяем что запрос существует
        requestCache.find(rq.getRequest());

        // проверяем что время сигнала позже чем текущий момент
        if (rq.getSignal()
                .compareTo(DateUtils.convert(new Date(), DateUtils.HUMAN_DATE_TIME)) <= 0) throw new GBadRequest();

        RequestReminder requestReminder = RequestReminder.builder()
                .request(rq.getRequest())
                .body(rq.getBody())
                .checked(false)
                .signal(rq.getSignal())
                .created(DateUtils.convert(new Date(), DateUtils.HUMAN_DATE_TIME))
                .build();

        cache.save(requestReminder);
        return RsRequestReminder.getFromEntity(requestReminder);
    }

    public RsRequestReminder postpone(RqRequestReminderPostpone rq) throws GNotAllowed, GNotFound, GSystemError, GBadRequest {

        // проверяем что напоминание есть
        RequestReminder requestReminder = cache.find(rq.getId());

        // проверяем что новое время сигнала позже чем текущий момент
        if (rq.getNewSignal().
                compareTo(DateUtils.convert(new Date(), DateUtils.HUMAN_DATE_TIME)) <= 0) throw new GBadRequest();

        requestReminder.setSignal(rq.getNewSignal());
        cache.save(requestReminder);
        return RsRequestReminder.getFromEntity(requestReminder);
    }

    public GResponse disable(Long reminder) throws GNotAllowed, GNotFound, GSystemError {
        RequestReminder requestReminder = cache.find(reminder);
        requestReminder.setDisabled(DateUtils.convert(new Date(), DateUtils.HUMAN_DATE_TIME));
        cache.save(requestReminder);
        return new GResponse(OK);
    }
}
