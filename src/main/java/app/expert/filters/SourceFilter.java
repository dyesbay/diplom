package app.expert.filters;

import app.base.filters.GFilter;
import app.base.services.GContextService;
import lombok.SneakyThrows;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static app.base.constants.GConstants.HEADER_REQUESTER;
import static app.base.constants.GConstants.HEADER_SYSTEM;

@Component
@Order(10)
public class SourceFilter extends GFilter {

    private final GContextService contexts;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public SourceFilter(GContextService contexts) {
        this.contexts = contexts;
    }

    private void doFilterUser(HttpServletRequest request,
                              HttpServletResponse response,
                              FilterChain chain) throws IOException, ServletException {
        // приходит реквест
        // вытаскиваем из заголовка значение системы и источника и добавляем в контекст
        contexts.setRequester(request.getHeader(HEADER_REQUESTER));

        // проверить если source == system, то system == telephony
        if (contexts.getRequester() != null && contexts.getRequester().equals("system")) {
            String system = request.getHeader(HEADER_SYSTEM);
            contexts.setSystem(system);
        }
        chain.doFilter(request, response);
    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {

        doFilterUser((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, chain);
    }
}
