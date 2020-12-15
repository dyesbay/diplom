package app.expert.models.request;

import app.expert.db.request.Request;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Component
public class RequestFilter implements Specification<Request> {
    
    private List<Predicate> predicates = new LinkedList<>();
    private final static int DEFAULT_PAGE_SIZE = 1;
    
    private int pageNumber;
    private int pageSize;
    
    public RequestFilter() {
       this.pageSize = DEFAULT_PAGE_SIZE;
    }

    @Override
    public Predicate toPredicate(Root<Request> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        buildPredicates(root, cb);
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }
    
    private void buildPredicates(Root<Request> root, CriteriaBuilder cb) {
        /*
         * Здесь можно добавить предикаты для фильтрации запросов
         * 
         * */
    }
}
