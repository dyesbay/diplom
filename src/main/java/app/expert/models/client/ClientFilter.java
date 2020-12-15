package app.expert.models.client;

import app.base.models.GFilter;
import app.expert.db.client.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class ClientFilter extends GFilter<Long, Client> {

    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private String name;
    private String company;

    @Override
    public List<Predicate> getPredicates(Root<Client> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new LinkedList<>();
        if (phone != null) predicates.add(cb.equal(root.get("phone"), phone));
        if (email != null) predicates.add(cb.like(root.get("email"), "%" + email + "%"));
        if (firstName != null) predicates.add(cb.like(root.get("firstName"), "%" + firstName + "%"));
        if (lastName != null) predicates.add(cb.like(root.get("lastName"), "%" + lastName + "%"));
        if (company != null) predicates.add(cb.like(root.get("company"), "%" + company + "%"));
        if (name != null) {
            String[] pieces = name.split(" ");
            List<Predicate> namePredicates = new ArrayList<>();
            for (String piece : pieces) {
                namePredicates.add(cb.or(cb.like(cb.lower(root.get("firstName")), "%" + piece.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("lastName")), "%" + piece.toLowerCase() + "%")));
            }
            predicates.add(cb.and(namePredicates.toArray(new Predicate[0])));
        }
        predicates.add(cb.isNull(root.get("disabledOn")));
        return predicates;
    }
}
