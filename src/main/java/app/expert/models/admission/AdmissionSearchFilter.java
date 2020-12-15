package app.expert.models.admission;

import app.base.models.GFilter;
import app.base.utils.DateUtils;
import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.db.admission.AdmissionSearchEntity;
import app.expert.db.admission.CallSearchEntity;
import app.expert.db.admission.CommunicationMethod;
import app.expert.db.client.Client;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static app.base.utils.ObjectUtils.isNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class AdmissionSearchFilter extends GFilter<Long, AdmissionSearchEntity> {

    @DateTimeFormat(pattern=DateUtils.HUMAN_DATE)
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Moscow")
    private Date from;

    @DateTimeFormat(pattern= DateUtils.HUMAN_DATE)
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Moscow")
    private Date to;

    private Long subject;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String clientType;
    private Channel channel;

    private Long id;
    private Platform platform;
    private String routeType;
    private CommunicationMethod communicationMethod;
    private String status;
    private Long assignee;
    private String keys;

    @Override
    public List<Predicate> getPredicates(Root<AdmissionSearchEntity> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new LinkedList<>();
        Join<AdmissionSearchFilter, Client> clients = root.join("client", JoinType.LEFT);
        Join<AdmissionSearchFilter, CallSearchEntity> calls = root.join("callSearchEntity", JoinType.LEFT);
        if(!isNull(id))
            predicates.add(cb.equal(root.get("id"), this.id));
        if (!isNull(clientPhone))
            predicates.add(cb.like(clients.get("phone"), "%" + this.clientPhone + "%"));
        if (!isNull(clientEmail))
            predicates.add(cb.like(cb.lower(clients.get("email")), "%" + this.clientEmail.toLowerCase() + "%"));
        if(!isNull(clientType))
            predicates.add(cb.equal(clients.get("clientType"), this.clientType));
        if (!isNull(from))
            predicates.add(cb.greaterThanOrEqualTo(root.get("created"), this.from));
        if (!isNull(to))
            predicates.add(cb.lessThanOrEqualTo(root.get("created"), this.to));
        if (!isNull(subject))
            predicates.add(cb.equal(root.get("subject"), this.subject));
        if(!isNull(platform))
            predicates.add(cb.equal(calls.get("manager").get("platform"), this.platform.getKey()));
        if (!isNull(clientName)) {
            Arrays.stream(clientName.split(" ")).forEach(s -> {
                predicates.add(cb.or(
                        cb.like(cb.lower(clients.get("firstName")), "%" + s.toLowerCase() + "%"),
                        cb.like(cb.lower(clients.get("lastName")), "%" + s.toLowerCase() + "%"),
                        cb.like(cb.lower(clients.get("middleName")), "%" + s.toLowerCase() + "%")
                ));
            });
        }

        //Исключаем все результаты выборки,
        // если указаны поля не относящиеся к обращениям
        if(!isNull(routeType)
                || !isNull(communicationMethod)
                || !isNull(status)
                || !isNull(assignee)
                || !isNull(keys)) {
            predicates.add(cb.lessThanOrEqualTo(root.get("created"), new Date(0L)));
        }
        return predicates;
    }

    @Override
    public PageRequest getPageRequest() {
        return new PageRequest(getPage(), getSize(), Sort.Direction.DESC, "created");
    }
}
