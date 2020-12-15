package app.expert.models;

import app.base.models.GFilter;
import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.constants.State;
import app.expert.db.admission.CommunicationMethod;
import app.expert.db.client.Client;
import app.expert.db.request.RequestSearchEntity;
import app.expert.db.statics.status.Status;
import app.expert.validation.GPhoneParser;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static app.base.utils.ObjectUtils.isNull;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestFilter extends GFilter<Long, RequestSearchEntity> {

    private Long id;
    private Long subject;
    private Platform platform;
    private Channel channel;
    private String routeType;
    private CommunicationMethod communicationMethod;
    private String status;
    private Long assignee;
    private String keys;
    private String clientType;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private boolean includeClosedRequests;
    private List<State> stateList;

    @Override
    public List<Predicate> getPredicates(Root<RequestSearchEntity> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<RequestSearchEntity, Client> clients = root.join("requester", JoinType.LEFT);
        Join<RequestSearchEntity, Status> statuses = root.join("status", JoinType.LEFT);
        if (!isNull(id))
            predicates.add(cb.equal(root.get("id"), id));
        if (!isNull(status))
            predicates.add(cb.equal(statuses.get("code"), status));
        if (!isNull(subject))
            predicates.add(cb.equal(root.get("subject"), subject));
        if (!isNull(channel))
            predicates.add(cb.equal(root.get("channel"), channel));
        if (!isNull(routeType))
            predicates.add(cb.equal(statuses.get("routeType"), routeType));
        if (!isNull(from)) {
            predicates.add((cb.greaterThanOrEqualTo(root.get("created"), from)));
        }
        if (!isNull(to)) {
            predicates.add((cb.lessThanOrEqualTo(root.get("created"), to)));
        }
        if (!isNull(assignee))
            predicates.add(cb.equal(root.get("assignee"), assignee));
        if (!isNull(platform))
            predicates.add(cb.equal(root.get("platform"), platform));
        if (!isNull(clientPhone))
            predicates.add(cb.equal(clients.get("phone"), GPhoneParser.parsePhone(clientPhone)));
        if (!isNull(clientEmail))
            predicates.add(cb.equal(clients.get("email"), clientEmail));
        if(!isNull(clientType))
            predicates.add(cb.equal(clients.get("clientType"), clientType));
        if (!isNull(keys)) {
            Arrays.stream(keys.split(" ")).forEach(s -> {
                predicates.add(cb.like(cb.lower(root.get("text")), "%" + s.toLowerCase() + "%"));
            });
        }
        if (stateList != null) {
            predicates.add(cb.or(statuses.get("state").in(stateList)));
        }
        if (!isNull(clientName)) {
            Arrays.stream(clientName.split(" ")).forEach(s -> {
                predicates.add(cb.or(
                        cb.like(cb.lower(clients.get("firstName")), "%" + s.toLowerCase() + "%"),
                        cb.like(cb.lower(clients.get("lastName")), "%" + s.toLowerCase() + "%"),
                        cb.like(cb.lower(clients.get("middleName")), "%" + s.toLowerCase() + "%")
                ));
            });
        }
        if(!includeClosedRequests){
            predicates.add(cb.notEqual(statuses.get("state"), State.CLOSED));
        }
        return predicates;
    }

    @Override
    public PageRequest getPageRequest() {
        return new PageRequest(getPage(), getSize(), Sort.Direction.DESC, "created");
    }
}
