package app.expert.models;

import app.base.exceptions.GSystemError;
import app.base.services.GContextService;
import app.base.utils.DateUtils;
import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.constants.State;
import app.expert.db.admission.CommunicationMethod;
import app.expert.models.admission.AdmissionSearchFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static app.base.utils.ObjectUtils.isNull;

@Data
@Builder
@AllArgsConstructor
public class RqRequestAdmission {

    Integer page;
    Integer size;
    Long subject;
    Platform platform;
    Channel channel;
    String routeType;
    CommunicationMethod communicationMethod;
    @DateTimeFormat(pattern= DateUtils.HUMAN_DATE)
    Date from;
    @DateTimeFormat(pattern= DateUtils.HUMAN_DATE)
    Date to;
    String status;
    Long number;
    String clientPhone;
    String clientEmail;
    String clientName;
    String clientType;
    Long assignee;
    String keys;
    Boolean includeClosedRequests;
    Boolean includeRequests;
    Boolean includeAdmissions;
    Boolean forCurrentUser;
    List<State> stateList;

    public RqRequestAdmission() {
        page = 0;
        size = 10;
        includeRequests = true;
        includeAdmissions = true;
        forCurrentUser = false;
        includeClosedRequests = false;
    }

    public RequestFilter getRequestFilter(GContextService contexts) throws GSystemError {
        RequestFilter filter = new RequestFilter();
        filter.setSize(size);
        filter.setPage(page);
        filter.setPlatform(platform);
        filter.setSubject(subject);
        filter.setChannel(channel);
        filter.setRouteType(routeType);
        filter.setCommunicationMethod(communicationMethod);
        filter.setKeys(keys);
        filter.setClientEmail(clientEmail);
        filter.setClientPhone(clientPhone);
        filter.setClientName(clientName);
        filter.setClientType(clientType);
        filter.setStateList(stateList);
        filter.setIncludeClosedRequests(includeClosedRequests);
        if (from != null && to != null && from.compareTo(to) == 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(to);
            c.add(Calendar.DATE, 1);
            to = c.getTime();
        }
        filter.setFrom(getDateInFormat(from));
        filter.setTo(getDateInFormat(to));
        filter.setStatus(status);
        filter.setId(number);
        if (forCurrentUser)
            filter.setAssignee(contexts.getUser());
        else if (!isNull(assignee))
            filter.setAssignee(assignee);
        return filter;
    }

    public AdmissionSearchFilter getAdmissionFilter(GContextService contexts) throws GSystemError {
        AdmissionSearchFilter filter = new AdmissionSearchFilter();
        filter.setSize(size);
        filter.setPage(page);
        filter.setPlatform(platform);
        filter.setSubject(subject);
        filter.setChannel(channel);
        filter.setRouteType(routeType);
        filter.setCommunicationMethod(communicationMethod);
        filter.setKeys(keys);
        filter.setClientEmail(clientEmail);
        filter.setClientPhone(clientPhone);
        filter.setClientName(clientName);
        filter.setClientType(clientType);
        if (from != null && to != null && from.compareTo(to) == 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(to);
            c.add(Calendar.DATE, 1);
            to = c.getTime();
        }
        filter.setFrom(getDateInFormat(from));
        filter.setTo(getDateInFormat(to));
        filter.setStatus(status);
        filter.setId(number);
        if (forCurrentUser)
            filter.setAssignee(contexts.getUser());
        else if (!isNull(assignee))
            filter.setAssignee(assignee);
        return filter;
    }

    private Date getDateInFormat(Date date) throws GSystemError {
        if (date == null) return null;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date now;
        try {
            now = sf.parse(sf.format(date));
        } catch (ParseException e) {
            throw new GSystemError();
        }
        return now;
    }
}
