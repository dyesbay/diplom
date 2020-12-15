package app.expert.models.manager_address_book;

import app.base.models.GFilter;
import app.expert.db.manager_address_book.ManagerAddressBook;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class ManagerAddressBookFilter extends GFilter<Long, ManagerAddressBook> {

    private String firstName;
    private String lastName;
    private String position;

    @Override
    public List<Predicate> getPredicates(Root<ManagerAddressBook> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new LinkedList<>();
        if (firstName != null)
            predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        if (lastName != null)
            predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        if (position != null)
            predicates.add(cb.like(cb.lower(root.get("position")), "%" + position.toLowerCase() + "%"));
        return predicates;
    }
}
