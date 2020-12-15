package app.expert.controllers;

import app.base.controllers.GControllerAdvice;
import app.base.services.GContextService;
import app.base.utils.JwtUtils;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Api(tags = "----- Jwt Operations")
@RequestMapping("/jwt")
public class JwtExplorer extends GControllerAdvice {

    private final GContextService context;

    @GetMapping("/getInfoFromToken")
    public String getAgent() {
        return context.getAgentId() + " " + context.getSystem();
    }

    @GetMapping("/generateToken")
    public String generateToken(@RequestParam String requester, String agent_id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("subject", requester);
        if (agent_id != null)
            claims.put("agent_id", agent_id);
        return JwtUtils.generateSystemToken(claims);
    }
}
