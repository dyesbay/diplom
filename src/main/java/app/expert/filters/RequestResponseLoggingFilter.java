package app.expert.filters;

import app.base.constants.GErrors;
import app.base.services.GContextService;
import app.expert.constants.LogType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(2)
@RequiredArgsConstructor
public class RequestResponseLoggingFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final GContextService context;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        context.setContext(req);
        context.setLogType(LogType.REQUEST.getValue());
        logger.info("Request {} {}", req.getMethod(), req.getRequestURI());
        context.setLogType(LogType.OTHER.getValue());
        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long end = System.currentTimeMillis();
        context.setLogType(LogType.RESPONSE.getValue());
        logger.info("Response {} {}, status {} - {} , exec time: {} millis", req.getMethod(), req.getRequestURI(), res.getStatus(),
                context.getErrorCode() != null ? context.getErrorCode() : GErrors.OK.getKey(), end - start);
        context.setLogType(LogType.OTHER.getValue());
        MDC.clear();
    }

    @Override
    public void destroy() {
    }
}
