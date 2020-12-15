package app.expert.models.call;

import app.base.exceptions.GNotAllowed;
import app.base.exceptions.GNotFound;
import app.expert.db.call.Call;
import app.expert.db.manager.ManagerCache;
import app.expert.validation.GPhone;
import app.expert.validation.GPhoneParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Component;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Date;


@RequiredArgsConstructor
@Setter
@Getter
@Component
public class RqRegCall {

    @Column(name = "started_on")
    private Date started;

    @NotBlank
    private String identifier;

    @GPhone
    private String phone;

    private String stationId;

    @NotNull
    private String agentId;

    private CallType callType;
    
    public Call getCall(ManagerCache manCache) throws GNotFound, GNotAllowed {
        return Call.builder()
            .started(started)
            .identifier(identifier)
            .phone(GPhoneParser.parsePhone(phone))
            .stationId(stationId)
            .agentId(agentId)
            .identifier(identifier)
            .incoming(callType.getValue())
            .manager(manCache.findByAgentId(agentId).getId())
            .build();
    }
}
