package app.expert.models;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

public class SearchPageable implements Pageable, Serializable {

    private static final long serialVersionUID = -25822477129613575L;

    private final int limit;
    private final int offset;
    private final Sort sort;

    public SearchPageable(int offset, int limit, Sort sort) {

        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public SearchPageable(int offset, int limit, Sort.Direction direction, String... properties) {
        this(offset, limit, new Sort(direction, properties));
    }

    public SearchPageable(int offset, int limit) {
        this(offset, limit, new Sort(Sort.Direction.ASC));
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new SearchPageable(getOffset() + getPageSize(), getPageSize(), getSort());
    }

    public SearchPageable previous() {
        return hasPrevious() ? new SearchPageable(getOffset() - getPageSize(), getPageSize(), getSort()) : this;
    }


    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ? previous() : first();
    }

    @Override
    public Pageable first() {
        return new SearchPageable(0, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof SearchPageable)) return false;

        SearchPageable that = (SearchPageable) o;

        return new EqualsBuilder()
                .append(limit, that.limit)
                .append(offset, that.offset)
                .append(sort, that.sort)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(limit)
                .append(offset)
                .append(sort)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("limit", limit)
                .append("offset", offset)
                .append("sort", sort)
                .toString();
    }
}
