package app.expert.filters;

import app.base.exceptions.GException;
import app.base.filters.GFilter;
import app.base.models.GResponse;
import app.base.objects.IGEnum;
import app.base.services.GContextService;
import app.expert.constants.ExpertErrors;
import app.expert.db.manager.Manager;
import app.expert.db.manager.ManagerCache;
import app.expert.db.statics.manager_role.ManagerRole;
import app.expert.db.statics.manager_role_route.ManagerRoleRoute;
import app.expert.db.statics.route.Route;
import app.expert.db.statics.route.RouteRepository;
import app.expert.services.ManagerRoleRouteService;
import app.expert.services.ManagerRoleService;
import app.expert.services.RouteService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(50)
public class PathCheckFilter extends GFilter {

    private static final Logger logger = LoggerFactory.getLogger(PathCheckFilter.class);

    private final GContextService contexts;
    private final ManagerCache managerCache;
    private final RouteService routeService;
    private final ManagerRoleRouteService managerRoleRouteService;
    private final ManagerRoleService managerRoleService;
    private final RouteRepository routeRepository;

    @Value("${security.ignored:[]}")
    private String[] ignored;
    private List<String> ignore;

    public PathCheckFilter(GContextService contexts, ManagerCache managerCache, RouteService routeService,
                           ManagerRoleRouteService managerRoleRouteService, ManagerRoleService managerRoleService, RouteRepository routeRepository) {
        this.contexts = contexts;
        this.managerCache = managerCache;
        this.routeService = routeService;
        this.managerRoleRouteService = managerRoleRouteService;
        this.managerRoleService = managerRoleService;
        this.routeRepository = routeRepository;
    }

    public GResponse getErrorBody(IGEnum code) {
        return new GResponse(code);
    }

    private boolean check(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws GException, IOException {

        Manager manager = managerCache.find(contexts.getUser());
        ManagerRole role = managerRoleService.get(manager.getRole());
        if (role == null) {
            fillResponse(servletResponse, HttpStatus.BAD_REQUEST.value(),
                    getErrorBody(ExpertErrors.MANAGER_ROLE_NOT_FOUND));
            contexts.setErrorCode(ExpertErrors.MANAGER_ROLE_NOT_FOUND.name());
            return false;
        }
        Route route = routeService.findByMethodAndPath(
                servletRequest.getMethod().toLowerCase(),
                servletRequest.getServletPath()).orElse(null);
        if (route == null) {
            fillResponse(servletResponse, HttpStatus.NOT_FOUND.value(),
                    getErrorBody(ExpertErrors.PATH_NOT_FOUND));
            contexts.setErrorCode(ExpertErrors.PATH_NOT_FOUND.name());
            return false;
        }
        List<ManagerRole> managerRolesForRoute = managerRoleRouteService.getByRouteId(route.getId())
                .stream().map(ManagerRoleRoute::getManagerRole).collect(Collectors.toList());
//        if (!managerRolesForRoute.contains(role)) {
//            fillResponse(servletResponse, HttpStatus.METHOD_NOT_ALLOWED.value(),
//                    getErrorBody(ExpertErrors.ACCESS_DENIED));
//            contexts.setErrorCode(ExpertErrors.ACCESS_DENIED.name());
//            return false;
//        }
        return true;
    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) {

        String path = ((HttpServletRequest) servletRequest).getServletPath();
        boolean doFilter = true;
        boolean contains = false;

        ignore = routeRepository.findAllByOpen(true).stream().map(pattern -> {
            if (pattern.getPath().endsWith("/**")) {
                return pattern.getPath().replace("/**", "");
            }
            return pattern.getPath();
        }).collect(Collectors.toList());
        ignore.addAll(Arrays.stream(ignored).map(pattern -> {
            if (pattern.endsWith("/**")) {
                return pattern.replace("/**", "");
            }
            return pattern;
        }).collect(Collectors.toList()));

        for (String pattern : ignore) {
            if (pattern.equals(path) || path.startsWith(pattern) || path.contains(pattern)
            || (contexts.getSystem() != null && contexts.getSystem().equals("telephony"))) {
                contains = true;
                break;
            }
        }
        if (!contains) {
            doFilter = check((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        }
        if (doFilter) {
            chain.doFilter(servletRequest, servletResponse);
        }
    }
}
