package app.expert.models;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class SearchPage<T> extends PageImpl<T> {

    private Long adNum;
    private Long rqNum;

    public SearchPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public SearchPage(List<T> content) {
        super(content);
    }

    public SearchPage(List content, Pageable pageable, long total, long adNum, long rqNum) {
        super(content, pageable, total);
        this.adNum = adNum;
        this.rqNum = rqNum;
    }

    public Long getAdNum() {
        return adNum;
    }

    public Long getRqNum() {
        return rqNum;
    }
}
