package app.expert.models.department_address_book;

import app.base.models.GFilter;
import app.expert.db.statics.department_address_book.DepartmentAddressBook;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class DepartmentFilter extends GFilter<Long, DepartmentAddressBook> {

    private String name;
    private String region;
    private String city;

    @Override
    public List<Predicate> getPredicates(Root<DepartmentAddressBook> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new LinkedList<>();
        if (name != null)
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        if (region != null)
            predicates.add(cb.like(cb.lower(root.get("region")), "%" + region.toLowerCase() + "%"));
        if (city != null)
            predicates.add(cb.like(cb.lower(root.get("city")), "%" + city.toLowerCase() + "%"));
        return predicates;
    }
}
