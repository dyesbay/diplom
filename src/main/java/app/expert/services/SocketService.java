package app.expert.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SocketService {

    private final RestTemplateBuilder builder;

    @Value("${info.socket.url}")
    private String url;


    private HttpHeaders getHeaders() {
        // create headers
        HttpHeaders headers = new HttpHeaders();
        // set `content-type` header
        headers.setContentType(MediaType.APPLICATION_JSON);
        // set `accept` header
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public void sendToUser(String userUuid, String content) {

        RestTemplate restTemplate = builder.build();

        url = url + "/notifyUser";

        HashMap<String, String> map = new HashMap<>();
        map.put("userUuid", userUuid);
        map.put("content", content);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(map, getHeaders());

        // send POST request
        restTemplate.postForEntity(url, entity, String.class);
    }

    public void sendToAll(String content) {
        RestTemplate restTemplate = builder.build();

        url = url + "/notifyAll";

        // build the request
        HttpEntity<String> entity = new HttpEntity<>(content, getHeaders());

        // send POST request
        restTemplate.postForEntity(url, entity, String.class);
    }

    public void sendToRole(String role, String content) {
        RestTemplate restTemplate = builder.build();

        url = url + "/notifyRole";

        HashMap<String, String> map = new HashMap<>();
        map.put("role", role);
        map.put("content", content);

        HttpEntity<Map<String, String>> entity =  new HttpEntity<>(map, getHeaders());

        // send POST request
        restTemplate.postForEntity(url, entity, String.class);
    }
}
