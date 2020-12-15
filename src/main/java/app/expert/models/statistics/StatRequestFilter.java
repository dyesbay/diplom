package app.expert.models.statistics;

import app.base.utils.DateUtils;
import app.expert.constants.Channel;
import app.expert.constants.Platform;
import app.expert.constants.State;
import app.expert.models.RequestFilter;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatRequestFilter {

    @NotNull
    @DateTimeFormat(pattern= DateUtils.HUMAN_DATE)
    private Date from;

    @NotNull
    @DateTimeFormat(pattern= DateUtils.HUMAN_DATE)
    private Date to;

    private Platform platform;
    private Channel channel;
    private List<State> stateList;

    public RequestFilter getRqFilter() {
        RequestFilter filter = new RequestFilter();
        filter.setPlatform(this.platform);
        filter.setChannel(this.channel);
        filter.setStateList(this.stateList);
        filter.setTo(this.to);
        filter.setFrom(this.from);
        return filter;
    }
}
