package app.expert.models.admission;

import app.base.models.GFilter;
import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.db.admission.CommunicationMethod;
import app.expert.db.client.Client;
import app.expert.db.request.RequestSearchEntity;
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
public class AdmissionFilter extends GFilter<Long, RequestSearchEntity> {

    private Long id;
    private Long subject;
    private Platform platform;
    private Channel channel;
    private CommunicationMethod communicationMethod;
    private String clientName;
    private String clientPhone;
    private String clientEmail;

    @Override
    public List<Predicate> getPredicates(Root<RequestSearchEntity> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        Join<RequestSearchEntity, Client> clients = root.join("requester", JoinType.LEFT);
        if (!isNull(clientName)) {
            Arrays.stream(clientName.split(" ")).forEach(s -> {
                predicates.add(cb.or(
                        cb.like(cb.lower(clients.get("firstName")), "%" + s.toLowerCase() + "%"),
                        cb.like(cb.lower(clients.get("lastName")), "%" + s.toLowerCase() + "%"),
                        cb.like(cb.lower(clients.get("middleName")), "%" + s.toLowerCase() + "%")
                ));
            });
        }
        return null;
    }

    @Override
    public PageRequest getPageRequest() {
        return new PageRequest(getPage(), getSize(), Sort.Direction.DESC, "created");
    }
}
