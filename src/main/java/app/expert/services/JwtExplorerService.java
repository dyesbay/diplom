package app.expert.services;

import app.base.services.GContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtExplorerService {

    private final GContextService context;

    public String getUserNameFromContext() {
        return context == null ? null : context.getAgentId();
    }
}
