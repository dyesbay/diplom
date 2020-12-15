package app.expert.models.region;


import app.base.models.GFilter;
import app.expert.db.statics.region.Region;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class RegionFilter extends GFilter<Long, Region> {

    private String name;

    @Override
    public List<Predicate> getPredicates(Root<Region> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new LinkedList<>();
        if (name != null) predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        predicates.add(cb.isNull(root.get("disabledOn")));
        return predicates;
    }
}
